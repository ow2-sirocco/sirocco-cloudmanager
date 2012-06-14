package org.ow2.sirocco.cloudmanager.core.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.ILockManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.IMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.INetworkManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.api.ISystemManager;
import org.ow2.sirocco.cloudmanager.core.api.IVolumeManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudEntity;
import org.ow2.sirocco.cloudmanager.model.cimi.CloudResource;
import org.ow2.sirocco.cloudmanager.model.cimi.ForwardingGroup;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Job.Status;
import org.ow2.sirocco.cloudmanager.model.cimi.JobCollection;
import org.ow2.sirocco.cloudmanager.model.cimi.LockItem;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.NetworkPort;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.System;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;

@Stateless
@Local(ILockManager.class)
@SuppressWarnings("unused")
public class LockManager implements ILockManager {

    private static Logger logger = Logger.getLogger(LockManager.class);

    public long lockTimeoutInSeconds = 10;

    public long lockWaitTimeInSeconds = 1;

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Resource
    private EJBContext ctx;

    @Resource
    private SessionContext sessionContext;



    /**
     * used to lock an object, **works in its own transaction**
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void lock(String targetId, String targetType) throws CloudProviderException {
        LockManager.logger.info("locking object " + targetId+" of type "+targetType);
        
        //is there already a lock, and if true, the lock is still valid?
        
        LockItem li=null;
        try {
            li = (LockItem) this.em
                    .createQuery(
                            "SELECT j FROM LockItem j WHERE j.lockedObjectId=:lockedObjectId and j.lockedObjectType=:lockedObjectType")
                    .setParameter("lockedObjectId",targetId)
                    .setParameter("lockedObjectType",targetType)
                    .getSingleResult();
        } catch (NoResultException e) {
            //no lock, we can lock it!
        }
        
        if (li!=null){
            //the object is locked, verifying that the lock is still valid
            //should have been automatically deleted by a scheduled utility task
            long lockedTime = li.getLockedTime().getTime() + lockTimeoutInSeconds * 1000;
            long currentTime = new Date().getTime();
            if (currentTime < lockedTime) {
                throw new CloudProviderException("unable to lock object " + targetId +" of type "+targetType);
            }else{
                //the lock has expired
                this.em.remove(li);
            }
        }
        
        //no lock, we can set a lock
        //if another locking attempt is happening on the same object at about the same time,
        //this lock or the other will fail thanks to the database unicity constraint
        
        LockItem lock=new LockItem();
        lock.setLockedObjectId(targetId);
        lock.setLockedObjectType(targetType);
        lock.setLockedTime(new Date());
        
        try{
            this.em.persist(lock);
        }catch(RuntimeException e){
            throw new CloudProviderException("unable to lock object " + targetId +" of type "+targetType);
        }
        catch(Throwable e){
            throw new CloudProviderException("2unable to lock object " + targetId +" of type "+targetType);
        }
        
        try {
            Thread.sleep(lockWaitTimeInSeconds * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// development environment
        LockManager.logger.info("locked object " + targetId+" of type "+targetType);
        
    }

    /**
     * used to unlock an object, **works in its own transaction**
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void unlock(String targetId, String targetType) throws CloudProviderException {
        
        //we only remove an existing lockItem with the key targetId+targetType
        
        LockManager.logger.info("unlocking object " + targetId+" of type "+targetType);
        
        //is there already a lock?
        
        LockItem li=null;
        try {
            li = (LockItem) this.em
                    .createQuery(
                            "SELECT j FROM LockItem j WHERE j.lockedObjectId=:lockedObjectId and j.lockedObjectType=:lockedObjectType")
                    .setParameter("lockedObjectId",targetId)
                    .setParameter("lockedObjectType",targetType)
                    .getSingleResult();
        } catch (NoResultException e) {
            //no lock!
            throw new CloudProviderException("unable to unlock object " + targetId +" of type "+targetType+" because no lock exists!");
        }
        
        if (li!=null){
            this.em.remove(li);
        }
        
        try {
            Thread.sleep(lockWaitTimeInSeconds * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// development environment
        LockManager.logger.info("unlocked object " + targetId+" of type "+targetType);
        
    }

}
