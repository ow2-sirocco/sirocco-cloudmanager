package org.ow2.sirocco.cloudmanager.core.impl;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.ow2.sirocco.cloudmanager.core.api.IDatabaseManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteDatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Remote(IRemoteDatabaseManager.class)
@Local(IDatabaseManager.class)
public class DatabaseManager implements IDatabaseManager {
    private static Logger logger = LoggerFactory.getLogger(DatabaseManager.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Override
    public void cleanup() {
        this.em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE;").executeUpdate();
        DatabaseManager.logger.info("Cleaning up database");
        try {
            this.em.createNativeQuery("DELETE FROM Job").executeUpdate();

            this.em.createNativeQuery("DELETE FROM VolumeVolumeImage").executeUpdate();
            this.em.createNativeQuery("DELETE FROM VolumeConfiguration").executeUpdate();
            this.em.createNativeQuery("DELETE FROM VolumeImage").executeUpdate();
            this.em.createNativeQuery("DELETE FROM VolumeTemplate").executeUpdate();
            this.em.createNativeQuery("DELETE FROM Volume").executeUpdate();

            this.em.createNativeQuery("DELETE FROM CloudProviderLocation").executeUpdate();
            this.em.createNativeQuery("DELETE FROM CloudProviderAccount").executeUpdate();
            this.em.createNativeQuery("DELETE FROM CloudProvider").executeUpdate();

            this.em.createNativeQuery("DELETE FROM Machine").executeUpdate();
            this.em.createNativeQuery("DELETE FROM MachineConfiguration").executeUpdate();
            this.em.createNativeQuery("DELETE FROM MachineTemplate").executeUpdate();
            this.em.createNativeQuery("DELETE FROM MachineImage").executeUpdate();

            this.em.createNativeQuery("DELETE FROM Credentials").executeUpdate();

            this.em.createNativeQuery("DELETE FROM SYSTEMINSTANCE").executeUpdate();
            this.em.createNativeQuery("DELETE FROM SystemMachine").executeUpdate();
            this.em.createNativeQuery("DELETE FROM SystemVolume").executeUpdate();
            this.em.createNativeQuery("DELETE FROM SystemSystem").executeUpdate();
            this.em.createNativeQuery("DELETE FROM SystemTemplate").executeUpdate();

            this.em.createNativeQuery("DELETE FROM Credentials").executeUpdate();
            this.em.createNativeQuery("DELETE FROM CredentialsTemplate").executeUpdate();

            this.em.createNativeQuery("DELETE FROM Network").executeUpdate();
            this.em.createNativeQuery("DELETE FROM NetworkConfiguration").executeUpdate();
            this.em.createNativeQuery("DELETE FROM NetworkTemplate").executeUpdate();
            this.em.createNativeQuery("DELETE FROM NetworkPort").executeUpdate();
            this.em.createNativeQuery("DELETE FROM NetworkPortConfiguration").executeUpdate();
            this.em.createNativeQuery("DELETE FROM NetworkPortTemplate").executeUpdate();

            this.em.createNativeQuery("DELETE FROM Users").executeUpdate();
            this.em.createNativeQuery("DELETE FROM Tenant").executeUpdate();
        } catch (Exception e) {
            DatabaseManager.logger.error("Failed to delete some entities", e);
        } finally {
            this.em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE;").executeUpdate();
        }
    }

}
