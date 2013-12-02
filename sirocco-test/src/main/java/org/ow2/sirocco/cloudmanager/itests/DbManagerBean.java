/**
 *
 * SIROCCO
 * Copyright (C) 2013 Orange
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
 */
package org.ow2.sirocco.cloudmanager.itests;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@Local
public class DbManagerBean {

    @PersistenceContext
    private EntityManager em;

    public void cleanup() {
        try {
            this.em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE;").executeUpdate();

            this.em.createNativeQuery("DELETE FROM JOB").executeUpdate();

            this.em.createNativeQuery("DELETE FROM VOLUMEVOLUMEIMAGE").executeUpdate();
            this.em.createNativeQuery("DELETE FROM VOLUMECONFIGURATION").executeUpdate();
            this.em.createNativeQuery("DELETE FROM VOLUMEIMAGE").executeUpdate();
            this.em.createNativeQuery("DELETE FROM VOLUMETEMPLATE").executeUpdate();
            this.em.createNativeQuery("DELETE FROM VOLUME").executeUpdate();

            this.em.createNativeQuery("DELETE FROM CLOUDPROVIDERLOCATION").executeUpdate();
            this.em.createNativeQuery("DELETE FROM CLOUDPROVIDERACCOUNT").executeUpdate();
            this.em.createNativeQuery("DELETE FROM CLOUDPROVIDER").executeUpdate();

            this.em.createNativeQuery("DELETE FROM MACHINE").executeUpdate();
            this.em.createNativeQuery("DELETE FROM MACHINEVOLUME").executeUpdate();
            this.em.createNativeQuery("DELETE FROM MACHINENETWORKINTERFACE").executeUpdate();
            this.em.createNativeQuery("DELETE FROM MACHINECONFIGURATION").executeUpdate();
            this.em.createNativeQuery("DELETE FROM MACHINETEMPLATE").executeUpdate();
            this.em.createNativeQuery("DELETE FROM MACHINEIMAGE").executeUpdate();

            this.em.createNativeQuery("DELETE FROM CREDENTIALS").executeUpdate();

            this.em.createNativeQuery("DELETE FROM SYSTEMINSTANCE").executeUpdate();
            this.em.createNativeQuery("DELETE FROM SYSTEMMACHINE").executeUpdate();
            this.em.createNativeQuery("DELETE FROM SYSTEMVOLUME").executeUpdate();
            this.em.createNativeQuery("DELETE FROM SYSTEMSYSTEM").executeUpdate();
            this.em.createNativeQuery("DELETE FROM SYSTEMTEMPLATE").executeUpdate();

            this.em.createNativeQuery("DELETE FROM CREDENTIALS").executeUpdate();
            this.em.createNativeQuery("DELETE FROM CREDENTIALSTEMPLATE").executeUpdate();

            this.em.createNativeQuery("DELETE FROM NETWORK").executeUpdate();
            this.em.createNativeQuery("DELETE FROM NETWORKCONFIGURATION").executeUpdate();
            this.em.createNativeQuery("DELETE FROM NETWORKTEMPLATE").executeUpdate();
            this.em.createNativeQuery("DELETE FROM NETWORKPORT").executeUpdate();
            this.em.createNativeQuery("DELETE FROM NETWORKPORTCONFIGURATION").executeUpdate();
            this.em.createNativeQuery("DELETE FROM NETWORKPORTTEMPLATE").executeUpdate();
            this.em.createNativeQuery("DELETE FROM ADDRESS").executeUpdate();

            this.em.createNativeQuery("DELETE FROM Users").executeUpdate();
            this.em.createNativeQuery("DELETE FROM Tenant").executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE;").executeUpdate();
        }
    }

}
