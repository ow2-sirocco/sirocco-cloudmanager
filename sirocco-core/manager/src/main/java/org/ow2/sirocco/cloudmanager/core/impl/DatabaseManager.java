package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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

    private static String JPA_IMPL = "eclipselink";
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public String getUnderlyingDB() throws Exception{
    	EntityManagerFactory emf = em.getEntityManagerFactory();     
    	Map<String, Object> emfProperties = emf.getProperties();
    	
    	if ("eclipselink".equals(JPA_IMPL)){
    		String driverClass = (String)emfProperties.get("eclipselink.target-database");
    		return driverClass;
    	}

    	throw new Exception("JPA IMPLEMENTATION NOT SUPPORTED");
    	
    }

    @Override
    public void cleanup() throws Exception {
    	
    	
    	String underlyingDB=getUnderlyingDB().toLowerCase();
    	
    	if (underlyingDB.contains("h2")){
    		this.em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE;").executeUpdate();		
    	}else if (underlyingDB.contains("mysql")){
    		this.em.createNativeQuery("SET foreign_key_checks = 0;").executeUpdate();	
    	}
    	
        DatabaseManager.logger.info("Cleaning up database");
        try {
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

            this.em.createNativeQuery("DELETE FROM Users").executeUpdate();
            this.em.createNativeQuery("DELETE FROM Tenant").executeUpdate();
        } catch (Exception e) {
            DatabaseManager.logger.error("Failed to delete some entities", e);
        } finally {
        	if (underlyingDB.contains("h2")){
        		this.em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE;").executeUpdate();		
        	}else if (underlyingDB.contains("mysql")){
        		this.em.createNativeQuery("SET foreign_key_checks = 1;").executeUpdate();	
        	}
        }
    }

}
