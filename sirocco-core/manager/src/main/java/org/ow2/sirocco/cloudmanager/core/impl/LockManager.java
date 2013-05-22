/**
 *
 * SIROCCO
 * Copyright (C) 2011 France Telecom
 * Contact: sirocco@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  $Id: LockManager.java 1296 2012-06-11 15:34:26Z ycas7461 $
 *
 */

package org.ow2.sirocco.cloudmanager.core.impl;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.sirocco.cloudmanager.core.api.ILockManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.model.cimi.LockItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Local(ILockManager.class)
@SuppressWarnings("unused")
public class LockManager implements ILockManager {

    private static Logger logger = LoggerFactory.getLogger(LockManager.class);

    public long lockTimeoutInSeconds = 600;

    @PersistenceContext(unitName = "siroccoPersistenceUnit", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Resource
    private EJBContext ctx;

    @Resource
    private SessionContext sessionContext;

    /**
     * to call internal methods by passing by the proxy.<br>
     * It is used to take care of transaction annotations on methods<br>
     * because if EJB method is called with <i>this</i>, it is not taken<br>
     * into account
     * 
     * @return
     */
    private ILockManager getThis() {
        return this.sessionContext.getBusinessObject(ILockManager.class);
    }

    /**
     * used to lock an object, **works in its own transaction**
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void lock(final String targetId, final String targetType) throws CloudProviderException {
        LockManager.logger.debug("locking object " + targetId + " of type " + targetType);

        // is there already a lock, and if true, the lock is still valid?
        LockItem li = null;
        try {
            li = (LockItem) this.em
                .createQuery(
                    "SELECT j FROM LockItem j WHERE j.lockedObjectId=:lockedObjectId and j.lockedObjectType=:lockedObjectType")
                .setParameter("lockedObjectId", targetId).setParameter("lockedObjectType", targetType).getSingleResult();
        } catch (NoResultException e) {
            // no lock, we can lock it!
        }

        if (li != null) {
            // the object is locked, verifying that the lock is still valid
            // should have been automatically deleted by a scheduled utility
            // task
            long lockedTime = li.getLockedTime().getTime() + this.lockTimeoutInSeconds * 1000;
            long currentTime = new Date().getTime();
            if (currentTime < lockedTime) {
                throw new CloudProviderException("unable to lock object "
                    + targetId
                    + " of type "
                    + targetType
                    + " because it is locked until "
                    + DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.getDefault()).format(
                        new Date(lockedTime)));
            } else {
                // the lock has expired
                this.em.remove(li);
            }
        }

        // no lock, we can set a lock
        // if another locking attempt is happening on the same object at about
        // the same time,
        // this lock or the other will fail thanks to the database unicity
        // constraint

        LockItem lock = new LockItem();
        lock.setLockedObjectId(targetId);
        lock.setLockedObjectType(targetType);
        lock.setLockedTime(new Date());

        try {
            this.em.persist(lock);
        } catch (Exception e) {
            throw new CloudProviderException("unable to lock object " + targetId + " of type " + targetType
                + " because of exception " + e.getMessage());
        }

        LockManager.logger.info("locked object " + targetId + " of type " + targetType);

    }

    /**
     * used to lock an object. maxRetryDelayInSeconds is used to allow this
     * method to do more than 1 attempt before throwing an exception
     */
    public void lock(final String targetId, final String targetType, final int maxRetryDelayInSeconds)
        throws CloudProviderException {

        boolean locked = false;
        CloudProviderException exc = new CloudProviderException("unknown exception");

        int nbRetry = 5;

        for (int i = 0; i < nbRetry; i++) {
            try {
                try {
                    if (i > 0) {
                        Thread.sleep((1000 * maxRetryDelayInSeconds) / nbRetry);
                    }
                } catch (InterruptedException e) {
                }
                this.getThis().lock(targetId, targetType);
                locked = true;
            } catch (CloudProviderException e) {
                locked = false;
                exc = e;
            }

            if (locked) {
                break;
            }
        }
        if (!locked) {
            throw exc;
        }

    }

    /**
     * used to unlock an object, **works in its own transaction**
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void unlockUntransacted(final String targetId, final String targetType) throws CloudProviderException {
        this._unlock(targetId, targetType, false);
    }

    /**
     * used to unlock an object, **works in the current transaction**
     */
    @Override
    public void unlock(final String targetId, final String targetType) throws CloudProviderException {
        this._unlock(targetId, targetType, true);
    }

    private void _unlock(final String targetId, final String targetType, final boolean transacted)
        throws CloudProviderException {
        // we only remove an existing lockItem with the key targetId+targetType
        LockManager.logger.debug("unlocking object " + targetId + " of type " + targetType + " - " + transacted);

        // is there already a lock?
        LockItem li = null;
        try {
            li = (LockItem) this.em
                .createQuery(
                    "SELECT j FROM LockItem j WHERE j.lockedObjectId=:lockedObjectId and j.lockedObjectType=:lockedObjectType")
                .setParameter("lockedObjectId", targetId).setParameter("lockedObjectType", targetType).getSingleResult();
        } catch (NoResultException e) {
            // no lock!
            throw new CloudProviderException("unable to unlock object " + targetId + " of type " + targetType
                + " because no lock exists!");
        }

        if (li != null) {
            this.em.remove(li);
        }

        LockManager.logger.info("unlocked object " + targetId + " of type " + targetType + " - " + transacted);
    }

}
