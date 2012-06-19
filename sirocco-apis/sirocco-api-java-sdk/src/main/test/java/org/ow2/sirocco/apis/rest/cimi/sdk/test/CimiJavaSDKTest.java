package org.ow2.sirocco.apis.rest.cimi.sdk.test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.Options;
import org.ow2.sirocco.apis.rest.cimi.sdk.Job;
import org.ow2.sirocco.apis.rest.cimi.sdk.Machine;
import org.ow2.sirocco.apis.rest.cimi.sdk.MachineConfiguration;
import org.ow2.sirocco.apis.rest.cimi.sdk.MachineConfiguration.Disk;
import org.ow2.sirocco.apis.rest.cimi.sdk.MachineCreate;
import org.ow2.sirocco.apis.rest.cimi.sdk.MachineImage;
import org.ow2.sirocco.apis.rest.cimi.sdk.MachineTemplate;

public class CimiJavaSDKTest {
    private String userName = "sirocco-test";

    private String password = "232908Ivry";

    private String cimiEndpointUrl = "http://localhost:9000/sirocco-rest/cimi";

    @Test
    public void testMachines() throws Exception {
        CimiClient client = CimiClient
            .login(this.cimiEndpointUrl, this.userName, this.password, Options.build().setDebug(true));

        List<Machine> machines = Machine.getMachines(client);

        Machine m = machines.get(0);

        //
        // create machine image
        //

        MachineImage machineImage = new MachineImage();
        machineImage.setType(MachineImage.Type.IMAGE);
        machineImage.setImageLocation("dummyId");
        machineImage.setName("Debian 6.0 64bits");
        machineImage.setDescription("Debian 6.0 64bits");
        machineImage.addProperty("cloudProviderId", "2");
        machineImage.addProperty("cloudProviderAccountId", "7");

        Job job = MachineImage.createMachineImage(client, machineImage);
        job.waitForCompletion(10L, TimeUnit.SECONDS);

        String machineImageRef = job.getTargetResourceRef();

        machineImage = MachineImage.getMachineImageByReference(client, machineImageRef);

        System.out.println("MachineImage: " + machineImage);

        List<MachineImage> machineImages = MachineImage.getMachineImages(client);

        //
        // create machine config
        //

        MachineConfiguration machineConfig = new MachineConfiguration();
        machineConfig.setName("Smallzzsddsddss");
        machineConfig.setDescription("OpenStack m1.small flavor");
        machineConfig.setCpu(1);
        machineConfig.setMemory(512);
        Disk[] disks = new Disk[] {new Disk(), new Disk()};
        disks[0].capacity = 10;
        disks[1].capacity = 20;
        machineConfig.setDisks(disks);

        machineConfig = MachineConfiguration.createMachineConfiguration(client, machineConfig);

        System.out.println("MachineConfig: " + machineConfig);

        //
        // create machine template
        //

        MachineTemplate machineTemplate = new MachineTemplate();
        machineTemplate.setName("Small Debian 6.0 64bitzddsddsz");
        machineTemplate.setDescription("Debian 6.0 and small config");
        machineTemplate.setMachineConfig(machineConfig);
        machineTemplate.setMachineImage(machineImage);
        machineTemplate.addProperty("color", "blue");

        machineTemplate = MachineTemplate.createMachineTemplate(client, machineTemplate);

        System.out.println("MachineTemplate: " + machineTemplate);

        //
        // create machine
        //

        machineTemplate = new MachineTemplate(client, "http://localhost:9000/sirocco-rest/cimi/machinesTemplates/99");

        MachineCreate machineCreate = new MachineCreate();
        machineCreate.setName("myMachine");
        machineCreate.setDescription("my machine");
        machineCreate.addProperty("usage", "dummy");
        machineCreate.setMachineTemplate(machineTemplate);

        job = Machine.createMachine(client, machineCreate);
        job.waitForCompletion(10L, TimeUnit.SECONDS);

        String machineRef = job.getTargetResourceRef();

        Machine machine = Machine.getMachineByReference(client, machineRef);

        System.out.println("Machine: " + machine);

        //
        // start machine
        //

        job = machine.start();

        job.waitForCompletion(10L, TimeUnit.SECONDS);

        machine = Machine.getMachineByReference(client, machineRef);

        System.out.println("Machine: " + machine);

        //
        // delete machine
        //

        job = machine.delete();

    }

}
