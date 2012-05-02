package org.ow2.sirocco.cloudmanager.model.cimi;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

@Entity
public class SystemTemplate extends CloudEntity {
    private static final long serialVersionUID = 1L;

    private Set<ComponentDescriptor> componentDescriptors;

    @OneToMany(mappedBy = "systemTemplate")
    public Set<ComponentDescriptor> getComponentDescriptors() {
        return componentDescriptors;
    }

    public void setComponentDescriptors(
            Set<ComponentDescriptor> componentDescriptors) {
        this.componentDescriptors = componentDescriptors;
    }

}
