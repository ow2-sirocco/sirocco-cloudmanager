package org.ow2.sirocco.apis.rest.cimi.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.MediaType;

import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient.Options;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiException;
import org.ow2.sirocco.apis.rest.cimi.sdk.Job;
import org.ow2.sirocco.apis.rest.cimi.sdk.Machine;
import org.ow2.sirocco.apis.rest.cimi.sdk.NetworkInterface;
import org.ow2.sirocco.apis.rest.cimi.sdk.System;
import org.ow2.sirocco.apis.rest.cimi.sdk.SystemCreate;
import org.ow2.sirocco.apis.rest.cimi.sdk.SystemMachine;
import org.ow2.sirocco.apis.rest.cimi.sdk.SystemTemplate;

public class SystemCIMITest {

    public static void main(final String[] args) {
        String cimiEndpointUrl = "http://p-sirocco:9000/sirocco-rest/cimi";
        String login = "guest";
        String password = "guest";
        String systemTemplateId = "http://p-sirocco:9000/sirocco-rest/cimi/systemTemplates/5";
        String provider = "openstack";
        String location = "France";

        try {
            Options options = Options.build();
            options.setDebug(true);
            options.setMediaType(MediaType.APPLICATION_XML_TYPE);
            CimiClient cimiClient = CimiClient.login(cimiEndpointUrl, login, password, options);

            SystemTemplate systemTemplate = SystemTemplate.getSystemTemplateByReference(cimiClient, systemTemplateId);

            SystemCreate systemCreate = new SystemCreate();
            systemCreate.setName("Springoo");
            systemCreate.setDescription("Springoo application");
            Map<String, String> props = new HashMap<String, String>();
            props.put("provider", provider);
            props.put("location", location);
            props.put("userData", "foobar");
            systemCreate.setProperties(props);
            SystemTemplate systemTemplateRef = new SystemTemplate(cimiClient, systemTemplate.getId());
            systemCreate.setSystemTemplate(systemTemplateRef);
            Job job = System.createSystem(cimiClient, systemCreate);

            try {
                job.waitForCompletion(600, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String systemId = job.getTargetResourceRef();
            System syst = System.getSystemByReference(cimiClient, systemId);

            for (SystemMachine systemMachine : syst.getMachines()) {
                Machine machine = systemMachine.getMachine();
                java.lang.System.out.println("Machine name=" + machine.getName());
                for (NetworkInterface nic : machine.getNetworkInterface()) {
                    java.lang.System.out.println(nic.getType() + "=" + nic.getIp() + " ");
                }
            }

        } catch (CimiException e) {
            e.printStackTrace();
        }

    }

}
