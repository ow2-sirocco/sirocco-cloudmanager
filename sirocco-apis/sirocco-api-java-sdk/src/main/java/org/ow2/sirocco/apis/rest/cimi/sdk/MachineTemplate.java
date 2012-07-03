package org.ow2.sirocco.apis.rest.cimi.sdk;

import java.util.ArrayList;
import java.util.List;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplate;
import org.ow2.sirocco.apis.rest.cimi.domain.CimiMachineTemplateCollection;
import org.ow2.sirocco.apis.rest.cimi.utils.ConstantsPath;

public class MachineTemplate extends Resource<CimiMachineTemplate> {
    private MachineImage machineImage;

    private MachineConfiguration machineConfig;

    public MachineTemplate() {
        super(null, new CimiMachineTemplate());
    }

    public MachineTemplate(final CimiClient cimiClient, final String id) {
        super(cimiClient, new CimiMachineTemplate());
        this.cimiObject.setHref(id);
        this.cimiObject.setId(id);
    }

    public MachineTemplate(final CimiClient cimiClient, final CimiMachineTemplate cimiObject) {
        super(cimiClient, cimiObject);
        this.machineImage = new MachineImage(cimiClient, cimiObject.getMachineImage());
        this.machineConfig = new MachineConfiguration(cimiClient, cimiObject.getMachineConfig());
    }

    public MachineImage getMachineImage() {
        return this.machineImage;
    }

    public void setMachineImage(final MachineImage machineImage) {
        this.machineImage = machineImage;
        this.cimiObject.setMachineImage(machineImage.cimiObject);
    }

    public MachineConfiguration getMachineConfig() {
        return this.machineConfig;
    }

    public void setMachineConfig(final MachineConfiguration machineConfig) {
        this.machineConfig = machineConfig;
        this.cimiObject.setMachineConfig(machineConfig.cimiObject);
    }

    public void delete() throws CimiException {
        this.cimiClient.deleteRequest(this.cimiClient.extractPath(this.getId()));
    }

    public static MachineTemplate createMachineTemplate(final CimiClient client, final MachineTemplate machineTemplate)
        throws CimiException {
        CimiMachineTemplate cimiObject = client.postRequest(ConstantsPath.MACHINE_TEMPLATE_PATH, machineTemplate.cimiObject,
            CimiMachineTemplate.class);
        return new MachineTemplate(client, cimiObject);
    }

    public static List<MachineTemplate> getMachineTemplates(final CimiClient client) throws CimiException {
        CimiMachineTemplateCollection machineTemplateCollection = client.getRequest(
            client.extractPath(client.cloudEntryPoint.getMachineTemplates().getHref()), CimiMachineTemplateCollection.class);

        List<MachineTemplate> result = new ArrayList<MachineTemplate>();

        if (machineTemplateCollection.getCollection() != null) {
            for (CimiMachineTemplate cimiMachineTemplate : machineTemplateCollection.getCollection().getArray()) {
                result.add(new MachineTemplate(client, cimiMachineTemplate));
            }
        }
        return result;
    }

    public static MachineTemplate getMachineTemplateByReference(final CimiClient client, final String ref) throws CimiException {
        return new MachineTemplate(client, client.getCimiObjectByReference(ref, CimiMachineTemplate.class));
    }

    public static MachineTemplate getMachineTemplateById(final CimiClient client, final String id) throws CimiException {
        String path = client.getMachineTemplatesPath() + "/" + id;
        return new MachineTemplate(client, client.getCimiObjectByReference(path, CimiMachineTemplate.class));
    }

}
