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
package org.ow2.sirocco.cloudmanager.itests.ejb;

import java.io.File;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.RejectDependenciesStrategy;
import org.junit.After;
import org.junit.Before;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager;
import org.ow2.sirocco.cloudmanager.core.api.ICloudProviderManager.CreateCloudProviderAccountOptions;
import org.ow2.sirocco.cloudmanager.core.api.IJobManager;
import org.ow2.sirocco.cloudmanager.core.api.ITenantManager;
import org.ow2.sirocco.cloudmanager.core.api.IUserManager;
import org.ow2.sirocco.cloudmanager.core.api.IdentityContext;
import org.ow2.sirocco.cloudmanager.itests.DbManagerBean;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProvider;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.CloudProviderProfile;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.Tenant;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;
import org.ow2.sirocco.cloudmanager.model.utils.SiroccoConfiguration;

public class AbstractTestBase {
    protected static final int ASYNC_OPERATION_WAIT_TIME_IN_SECONDS = 20;

    @Inject
    protected IdentityContext identityContext;

    @EJB
    protected DbManagerBean dbManagerBean;

    @EJB
    protected IUserManager userManager;

    @EJB
    protected ITenantManager tenantManager;

    @EJB
    protected ICloudProviderManager providerManager;

    @EJB
    protected IJobManager jobManager;

    @Before
    public void setUp() throws Exception {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin");

        Tenant tenant = new Tenant();
        tenant.setName("trial");
        tenant = this.tenantManager.createTenant(tenant);
        tenant = this.tenantManager.getTenantById(tenant.getId());

        user = new User();
        user.setUsername("guest");
        user.setPassword("guest");
        user = this.userManager.createUser(user);
        this.tenantManager.addUserToTenant(tenant.getUuid(), user.getUuid());

        this.identityContext.setUserName("guest");

        CloudProviderProfile providerProfile = new CloudProviderProfile();
        providerProfile.setType("mock");
        providerProfile.setDescription("Mock");
        providerProfile.setConnectorClass("org.ow2.sirocco.cloudmanager.connector.mock.MockCloudProviderConnector");
        this.providerManager.createCloudProviderProfile(providerProfile);

        CloudProvider provider = new CloudProvider();
        provider.setEndpoint("");
        provider.setCloudProviderType("mock");
        provider.setDescription("mock");
        provider = this.providerManager.createCloudProvider(provider);

        CloudProviderLocation location = new CloudProviderLocation();
        location.setIso3166_1("FR");
        location.setCountryName("France");
        this.providerManager.addLocationToCloudProvider(provider.getUuid(), location);

        CloudProviderAccount account = new CloudProviderAccount();
        account.setLogin("");
        account.setPassword("");
        CreateCloudProviderAccountOptions options = new CreateCloudProviderAccountOptions().importMachineConfigs(true)
            .importMachineImages(true).importNetworks(true);
        account = this.providerManager.createCloudProviderAccount(provider.getUuid(), account, options);

        this.providerManager.addCloudProviderAccountToTenant(tenant.getUuid(), account.getUuid());
    }

    @After
    public void cleanUp() {
        this.dbManagerBean.cleanup();
    }

    protected Job.Status waitForJobCompletion(Job job) throws Exception {
        int counter = AbstractTestBase.ASYNC_OPERATION_WAIT_TIME_IN_SECONDS;
        String jobUuid = job.getUuid();
        while (true) {
            job = this.jobManager.getJobByUuid(jobUuid);
            if (job.getState() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("Machine operation time out");
            }
        }
        return job.getState();
    }

    @Deployment
    public static WebArchive deploy() {
        File[] libs = Maven.resolver().offline().loadPomFromFile("pom.xml")
            .resolve("org.ow2.sirocco.cloudmanager:sirocco-cloudmanager-core-api").withoutTransitivity().asFile();
        File[] libs2 = Maven.resolver().offline().loadPomFromFile("pom.xml")
            .resolve("org.ow2.sirocco.cloudmanager:sirocco-cloudmanager-core-manager")
            .using(new RejectDependenciesStrategy(false, "org.ow2.sirocco.cloudmanager:sirocco-cloudmanager-model-cimi"))
            .asFile();
        File[] libs3 = Maven.resolver().offline().loadPomFromFile("pom.xml")
            .resolve("org.ow2.sirocco.cloudmanager:sirocco-cloudmanager-connector-mock").withoutTransitivity().asFile();

        WebArchive war = ShrinkWrap.create(WebArchive.class).addAsLibraries(libs).addAsLibraries(libs2).addAsLibraries(libs3)
            .addClass(DbManagerBean.class).addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addPackages(true, Machine.class.getPackage(), SiroccoConfiguration.class.getPackage())
            .addAsManifestResource("beans.xml");
        return war;
    }
}
