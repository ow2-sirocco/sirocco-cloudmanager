package org.ow2.sirocco.apis.rest.cimi.tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.ow2.sirocco.cloudmanager.core.api.IRemoteJobManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteMachineManager;
import org.ow2.sirocco.cloudmanager.core.api.IRemoteSystemManager;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineNetworkInterface;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineTemplate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.ComponentDescriptor;
import org.ow2.sirocco.cloudmanager.model.cimi.system.ComponentDescriptor.ComponentType;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemCreate;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemMachine;
import org.ow2.sirocco.cloudmanager.model.cimi.system.SystemTemplate;

import com.sun.appserv.security.ProgrammaticLogin;

public class BuildSystemTemplate {

    private IRemoteJobManager jobManager;

    private final String host = "localhost";

    private final String port = "3700";

    Context getContext() throws NamingException {
        Properties env = new Properties();

        env.setProperty("org.omg.CORBA.ORBInitialHost", this.host);
        env.setProperty("org.omg.CORBA.ORBInitialPort", this.port);
        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, AdminClient.GF_INITIAL_CONTEXT_FACTORY);

        ProgrammaticLogin programmaticLogin = new ProgrammaticLogin();
        programmaticLogin.login("guest", "guest");

        return new InitialContext(env);
    }

    public void start() throws Exception {
        Context context = this.getContext();
        IRemoteSystemManager systemManager = (IRemoteSystemManager) context.lookup(IRemoteSystemManager.EJB_JNDI_NAME);
        IRemoteMachineManager machineManager = (IRemoteMachineManager) context.lookup(IRemoteMachineManager.EJB_JNDI_NAME);
        this.jobManager = (IRemoteJobManager) context.lookup(IRemoteJobManager.EJB_JNDI_NAME);

        MachineTemplate machineTemplate = null;
        for (MachineTemplate template : machineManager.getMachineTemplates()) {
            if (template.getName().equals("small-vamp-springoo")) {
                machineTemplate = template;
                break;
            }
        }

        if (machineTemplate == null) {
            java.lang.System.out.println("Cannot find machine template");
            return;
        }

        Set<ComponentDescriptor> componentDescriptors = new HashSet<ComponentDescriptor>();

        ComponentDescriptor component = new ComponentDescriptor();
        component.setName("LoadBalancer");
        component.setComponentQuantity(1);
        component.setComponentType(ComponentType.MACHINE);
        component.setDescription("Apache front-end load-balancer");
        Map<String, String> props = new HashMap<String, String>();
        component.setProperties(props);
        component.setComponentTemplate(machineTemplate);
        componentDescriptors.add(component);

        component = new ComponentDescriptor();
        component.setName("ApplicationServer");
        component.setComponentQuantity(1);
        component.setComponentType(ComponentType.MACHINE);
        component.setDescription("JOnAS application server");
        props = new HashMap<String, String>();
        component.setProperties(props);
        component.setComponentTemplate(machineTemplate);
        componentDescriptors.add(component);

        component = new ComponentDescriptor();
        component.setName("MySQL");
        component.setComponentQuantity(1);
        component.setComponentType(ComponentType.MACHINE);
        component.setDescription("MySQL tier");
        props = new HashMap<String, String>();
        component.setProperties(props);
        component.setComponentTemplate(machineTemplate);
        componentDescriptors.add(component);

        SystemTemplate systemTemplate = new SystemTemplate();

        systemTemplate.setName("SpringooTemplate");
        systemTemplate.setDescription("Springoo 3-tiers application template");
        systemTemplate.setComponentDescriptors(componentDescriptors);

        systemManager.createSystemTemplate(systemTemplate);

        // SystemCreate systemCreate = new SystemCreate();
        // systemCreate.setName("Springoo");
        // systemCreate.setDescription("Springoo application");
        // props = new HashMap<String, String>();
        // props.put("provider", "amazon");
        // props.put("location", "Ireland");
        // systemCreate.setProperties(props);
        // systemCreate.setSystemTemplate(systemTemplate);
        //
        // Job job = systemManager.createSystem(systemCreate);
        //
        // this.waitForJobCompletion(job);

    }

    public void buildDMTemplate() throws Exception {
        Context context = this.getContext();
        IRemoteSystemManager systemManager = (IRemoteSystemManager) context.lookup(IRemoteSystemManager.EJB_JNDI_NAME);
        IRemoteMachineManager machineManager = (IRemoteMachineManager) context.lookup(IRemoteMachineManager.EJB_JNDI_NAME);
        this.jobManager = (IRemoteJobManager) context.lookup(IRemoteJobManager.EJB_JNDI_NAME);

        MachineTemplate machineTemplate = null;
        for (MachineTemplate template : machineManager.getMachineTemplates()) {
            if (template.getName().equals("small-vamp-deploymentmgr")) {
                machineTemplate = template;
                break;
            }
        }

        if (machineTemplate == null) {
            java.lang.System.out.println("Cannot find machine template");
            return;
        }

        Set<ComponentDescriptor> componentDescriptors = new HashSet<ComponentDescriptor>();

        ComponentDescriptor component = new ComponentDescriptor();
        component.setName("DeploymentManager");
        component.setComponentQuantity(1);
        component.setComponentType(ComponentType.MACHINE);
        component.setDescription("Deployement Manager machine");
        Map<String, String> props = new HashMap<String, String>();
        component.setProperties(props);
        component.setComponentTemplate(machineTemplate);
        componentDescriptors.add(component);

        SystemTemplate systemTemplate = new SystemTemplate();

        systemTemplate.setName("DeploymentManagerTemplate");
        systemTemplate.setDescription("VAMP Deployment Manager template");
        systemTemplate.setComponentDescriptors(componentDescriptors);

        systemManager.createSystemTemplate(systemTemplate);

        // SystemCreate systemCreate = new SystemCreate();
        // systemCreate.setName("Springoo");
        // systemCreate.setDescription("Springoo application");
        // props = new HashMap<String, String>();
        // props.put("provider", "amazon");
        // props.put("location", "Ireland");
        // systemCreate.setProperties(props);
        // systemCreate.setSystemTemplate(systemTemplate);
        //
        // Job job = systemManager.createSystem(systemCreate);
        //
        // this.waitForJobCompletion(job);

    }

    public void start444() throws Exception {
        Context context = this.getContext();
        IRemoteSystemManager systemManager = (IRemoteSystemManager) context.lookup(IRemoteSystemManager.EJB_JNDI_NAME);
        IRemoteMachineManager machineManager = (IRemoteMachineManager) context.lookup(IRemoteMachineManager.EJB_JNDI_NAME);
        this.jobManager = (IRemoteJobManager) context.lookup(IRemoteJobManager.EJB_JNDI_NAME);

        MachineTemplate machineTemplate = null;
        for (MachineTemplate template : machineManager.getMachineTemplates()) {
            if (template.getName().equals("micro-ubuntu")) {
                machineTemplate = template;
                break;
            }
        }

        if (machineTemplate == null) {
            java.lang.System.out.println("Cannot find machine template");
            return;
        }

        Set<ComponentDescriptor> componentDescriptors = new HashSet<ComponentDescriptor>();

        ComponentDescriptor component = new ComponentDescriptor();
        component.setName("CLIF supervisor");
        component.setComponentQuantity(1);
        component.setComponentType(ComponentType.MACHINE);
        component.setDescription("CLIF supervisor");
        Map<String, String> props = new HashMap<String, String>();
        component.setProperties(props);
        component.setComponentTemplate(machineTemplate);
        componentDescriptors.add(component);

        component = new ComponentDescriptor();
        component.setName("CLIF Load Injector 1");
        component.setComponentQuantity(1);
        component.setComponentType(ComponentType.MACHINE);
        component.setDescription("CLIF Load Injector 1");
        props = new HashMap<String, String>();
        component.setProperties(props);
        component.setComponentTemplate(machineTemplate);
        componentDescriptors.add(component);

        component = new ComponentDescriptor();
        component.setName("CLIF Load Injector 2");
        component.setComponentQuantity(1);
        component.setComponentType(ComponentType.MACHINE);
        component.setDescription("CLIF Load Injector 2");
        props = new HashMap<String, String>();
        component.setProperties(props);
        component.setComponentTemplate(machineTemplate);
        componentDescriptors.add(component);

        SystemTemplate systemTemplate = new SystemTemplate();

        systemTemplate.setName("Load ");
        systemTemplate.setDescription("Springoo 3-tiers application template");
        systemTemplate.setComponentDescriptors(componentDescriptors);

        systemManager.createSystemTemplate(systemTemplate);

        // SystemCreate systemCreate = new SystemCreate();
        // systemCreate.setName("Springoo");
        // systemCreate.setDescription("Springoo application");
        // props = new HashMap<String, String>();
        // props.put("provider", "amazon");
        // props.put("location", "Ireland");
        // systemCreate.setProperties(props);
        // systemCreate.setSystemTemplate(systemTemplate);
        //
        // Job job = systemManager.createSystem(systemCreate);
        //
        // this.waitForJobCompletion(job);

    }

    public void start22() throws Exception {
        Context context = this.getContext();
        IRemoteSystemManager systemManager = (IRemoteSystemManager) context.lookup(IRemoteSystemManager.EJB_JNDI_NAME);
        IRemoteMachineManager machineManager = (IRemoteMachineManager) context.lookup(IRemoteMachineManager.EJB_JNDI_NAME);
        this.jobManager = (IRemoteJobManager) context.lookup(IRemoteJobManager.EJB_JNDI_NAME);

        Set<ComponentDescriptor> componentDescriptors = new HashSet<ComponentDescriptor>();

        SystemTemplate systemTemplate = new SystemTemplate();

        systemTemplate.setName("Ubuntu-11.04-amd64");
        systemTemplate.setDescription("Ubuntu 11.04 server 64bit");
        systemTemplate.setComponentDescriptors(componentDescriptors);
        // systemTemplate
        // .setProviderAssignedId("https://10.114.6.1/api/vAppTemplate/vappTemplate-e7a13cb2-821f-4a1a-86e7-d2efbc41b591");

        systemTemplate
            .setProviderAssignedId("https://10.114.6.1/api/vAppTemplate/vappTemplate-189ea10e-cad8-4b78-a722-55ab83d96976");

        systemManager.createSystemTemplate(systemTemplate);

        systemTemplate.setName("Springoo");
        systemTemplate.setDescription("Springoo 3-tier application");
        systemTemplate.setComponentDescriptors(new HashSet<ComponentDescriptor>());
        systemTemplate
            .setProviderAssignedId("https://10.114.6.1/api/vAppTemplate/vappTemplate-e7a13cb2-821f-4a1a-86e7-d2efbc41b591");

        systemManager.createSystemTemplate(systemTemplate);

        // SystemCreate systemCreate = new SystemCreate();
        // systemCreate.setName("Springoo");
        // systemCreate.setDescription("Springoo application");
        // props = new HashMap<String, String>();
        // props.put("provider", "amazon");
        // props.put("location", "Ireland");
        // systemCreate.setProperties(props);
        // systemCreate.setSystemTemplate(systemTemplate);
        //
        // Job job = systemManager.createSystem(systemCreate);
        //
        // this.waitForJobCompletion(job);

    }

    public void example() throws Exception {
        // id of the system template to use
        // the template has been created offline in Sirocco
        String systemTemplateId = "5";

        Context context = this.getContext();
        IRemoteSystemManager systemManager = (IRemoteSystemManager) context.lookup(IRemoteSystemManager.EJB_JNDI_NAME);
        this.jobManager = (IRemoteJobManager) context.lookup(IRemoteJobManager.EJB_JNDI_NAME);

        SystemTemplate systemTemplate = systemManager.getSystemTemplateById(systemTemplateId);

        SystemCreate systemCreate = new SystemCreate();
        systemCreate.setName("Springoo");
        systemCreate.setDescription("Springoo application");
        Map<String, String> props = new HashMap<String, String>();
        props = new HashMap<String, String>();
        props.put("provider", "openstack");
        props.put("location", "France");
        props.put("userData", "foobar");
        systemCreate.setProperties(props);
        systemCreate.setSystemTemplate(systemTemplate);

        Job job = systemManager.createSystem(systemCreate);

        this.waitForJobCompletion(job);

        String systemId = job.getTargetResource().getId().toString();

        System system = systemManager.getSystemById(systemId);

        for (SystemMachine systemMachine : system.getMachines()) {
            Machine machine = (Machine) systemMachine.getResource();

            for (MachineNetworkInterface nic : machine.getNetworkInterfaces()) {
                java.lang.System.out.println("NIC type=" + nic.getNetworkType() + " IP address="
                    + nic.getAddresses().iterator().next());
            }
        }

        // job = systemManager.startSystem(system.getId().toString());
        //
        // this.waitForJobCompletion(job);

        // job = systemManager.stopSystem(system.getId().toString());
        //
        // this.waitForJobCompletion(job);
        //
        // job = systemManager.deleteSystem(system.getId().toString());
        //
        // this.waitForJobCompletion(job);

    }

    public void start4() throws Exception {
        Context context = this.getContext();
        IRemoteSystemManager systemManager = (IRemoteSystemManager) context.lookup(IRemoteSystemManager.EJB_JNDI_NAME);
        IRemoteMachineManager machineManager = (IRemoteMachineManager) context.lookup(IRemoteMachineManager.EJB_JNDI_NAME);
        this.jobManager = (IRemoteJobManager) context.lookup(IRemoteJobManager.EJB_JNDI_NAME);

        System system = systemManager.getSystems().get(0);

        Job job = systemManager.deleteSystem(system.getId().toString());

        this.waitForJobCompletion(job);

    }

    public void start77() throws Exception {
        Context context = this.getContext();
        IRemoteSystemManager systemManager = (IRemoteSystemManager) context.lookup(IRemoteSystemManager.EJB_JNDI_NAME);
        IRemoteMachineManager machineManager = (IRemoteMachineManager) context.lookup(IRemoteMachineManager.EJB_JNDI_NAME);
        this.jobManager = (IRemoteJobManager) context.lookup(IRemoteJobManager.EJB_JNDI_NAME);

        List<SystemTemplate> templates = systemManager.getSystemTemplates();
        for (SystemTemplate template : templates) {
            java.lang.System.out.println("SystemTemplate name=" + template.getName());
        }

        SystemTemplate template = systemManager.getSystemTemplateById("8");

        SystemCreate systemCreate = new SystemCreate();
        systemCreate.setName("Springoo");
        systemCreate.setDescription("Springoo application");
        // props = new HashMap<String, String>();
        // props.put("provider", "amazon");
        // props.put("location", "Ireland");
        // systemCreate.setProperties(props);
        systemCreate.setSystemTemplate(template);

        Job job = systemManager.createSystem(systemCreate);

        this.waitForJobCompletion(job);

    }

    void waitForJobCompletion(Job job) throws Exception {
        int counter = 60;
        String jobId = job.getId().toString();
        while (true) {
            job = this.jobManager.getJobById(jobId);
            if (job.getState() != Job.Status.RUNNING) {
                break;
            }
            Thread.sleep(1000);
            if (counter-- == 0) {
                throw new Exception("Operation time out");
            }
        }
    }

    public static void main(final String[] args) throws Exception {
        new BuildSystemTemplate().start();
        new BuildSystemTemplate().buildDMTemplate();
    }

}
