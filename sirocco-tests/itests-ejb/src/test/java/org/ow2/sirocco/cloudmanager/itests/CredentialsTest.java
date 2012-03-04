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
 *  $Id$
 *
 */
package org.ow2.sirocco.cloudmanager.itests;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ow2.sirocco.cloudmanager.core.api.ICredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteCredentialsManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Credentials;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.CredentialsTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

/**
 * This class requires the following system properties: -Dcarol.port=1099
 * -Ddbunit.connectionUrl=jdbc:mysql://localhost:3306/sirocco
 * -Ddbunit.driverClass=org.gjt.mm.mysql.Driver -Ddbunit.username=admcloud
 * -Ddbunit.password=admcloud -Ddbunit.schema=sirocco
 * -Ddbunit.dataset=src/sirocco-config/db/sirocco_db_empty.xml
 */
public class CredentialsTest {
    private static final String USER_NAME = "ANONYMOUS";

    private static final String ACCOUNT_USER = "machinetest";

    /**
     * Initial Context Factory.
     */
    private static final String INITIAL_CONTEXT_FACTORY = "org.ow2.carol.jndi.spi.MultiOrbInitialContextFactory";

    /**
     * Timeout (in seconds) for Sirocco to initialize.
     */
    private static final int INITIALIZE_TIMEOUT = 30;

    private IRemoteCredentialsManager credManager;

    private IRemoteUserManager userManager;

    Map<String, Credentials> creds = new HashMap<String, Credentials>();

    private void connectToCloudManager() throws Exception {
        String carolPortString = System.getProperty("carol.port");
        Assert.assertNotNull("carol.port not set!", carolPortString);
        int carolPort = Integer.parseInt(carolPortString);

        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, CredentialsTest.INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, "rmi://localhost:" + carolPort);
        final long timeout = System.currentTimeMillis() + CredentialsTest.INITIALIZE_TIMEOUT * 1000;
        while (true) {
            try {
                Context context = new InitialContext(env);
                this.userManager = (IRemoteUserManager) context.lookup(IUserManager.EJB_JNDI_NAME);
                this.credManager = (IRemoteCredentialsManager) context.lookup(ICredentialsManager.EJB_JNDI_NAME);
                break;
            } catch (NamingException e) {
                if (System.currentTimeMillis() > timeout) {
                    throw e;
                } else {
                    Thread.sleep(1000);
                }
            }
        }
    }

    private void setUpDatabase() throws Exception {
        PropertiesBasedJdbcDatabaseTester databaseTest;
        Reader reader = new FileReader(new File(System.getProperty("dbunit.dataset")));
        XmlDataSet dataSet = new XmlDataSet(reader);
        databaseTest = new PropertiesBasedJdbcDatabaseTester();
        databaseTest.setDataSet(dataSet);
        databaseTest.setSetUpOperation(DatabaseOperation.DELETE_ALL);
        databaseTest.onSetup();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.setUpDatabase();
        this.connectToCloudManager();
        // change password that is not validated by user manager
        User user = this.userManager.createUser("Lov", "Maps", "lov@maps.com", CredentialsTest.USER_NAME, "232908Ivry");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    private int credcounter = 1;

    private CredentialsCreate initCredentials() {
        CredentialsCreate credentialsCreate = new CredentialsCreate();
        CredentialsTemplate in = new CredentialsTemplate();
        credentialsCreate.setCredentialTemplate(in);
        credentialsCreate.setName("testCred_" + this.credcounter);
        credentialsCreate.setDescription("testCred_" + this.credcounter + " description ");
        credentialsCreate.setProperties(new HashMap<String, String>());
        in.setUserName("madras");
        in.setPassword("bombaydelhi");
        String key = new String("parisnewyork" + this.credcounter);
        in.setPublicKey(key.getBytes());
        this.credcounter += 1;
        return credentialsCreate;
    }

    private Credentials createCredentials(final CredentialsCreate in_c) throws Exception {

        Credentials out_c = this.credManager.createCredentials(in_c);
        Assert.assertNotNull("createCredentials returns no credentials", out_c);
        this.creds.put(out_c.getId().toString(), out_c);
        return out_c;
    }

    private boolean isPresent(final Credentials c) {
        Credentials c_list = this.creds.get(c.getId().toString());
        if (c_list == null) {
            return false;
        }
        return this.isEqual(c, c_list);

    }

    private boolean isEqual(final Credentials one, final Credentials two) {
        if (one.getUserName().equals(two.getUserName()) == false) {
            return false;
        }
        if (one.getPassword().equals(two.getPassword()) == false) {
            return false;
        }
        return true;
    }

    @Test
    public void testCredentials() throws Exception {

        Credentials c1 = this.createCredentials(this.initCredentials());

        Credentials c2 = this.createCredentials(this.initCredentials());

        Credentials c3 = this.createCredentials(this.initCredentials());

        Credentials c4 = this.createCredentials(this.initCredentials());
        // get credentials
        String cid = c2.getId().toString();
        System.out.println("testCredentials get creds for " + cid);
        Credentials c2_out = this.credManager.getCredentialsById(cid);
        System.out.println("testCredentials check if good " + c2_out.getId().toString());
        boolean e = this.isEqual(c2, c2_out);
        Assert.assertEquals(e, true);
        Assert.assertEquals(c2.getId(), c2_out.getId());

        // get credentials collection
        // CredentialsCollection credColl =
        // this.credManager.getCredentialsCollection();
        // List<Credentials> creds = credColl.getCredentials();
        // int count = 0;

        // for (Credentials c : creds) {
        // if (isPresent(c) == true) {
        // count++;
        // }
        // }
        // Assert.assertEquals(count, creds.size());

        // delete credentials
        System.out.println("testCredentials delete creds " + c1.getId().toString());
        this.deleteCredentials(c1.getId().toString());
        String deletedCid = c1.getId().toString();
        Credentials c1deleted_out = null;
        System.out.println("testCredentials reads creds again " + c1.getId().toString());
        try {
            c1deleted_out = this.credManager.getCredentialsById(deletedCid);
        } catch (Exception ex) {
            System.out.println(" Expected exception ");
        }
        Assert.assertNull(c1deleted_out);
        System.out.println("testCredentials completed ");
    }

    void deleteCredentials(final String credId) throws Exception {

        this.credManager.deleteCredentials(credId);
    }

}
