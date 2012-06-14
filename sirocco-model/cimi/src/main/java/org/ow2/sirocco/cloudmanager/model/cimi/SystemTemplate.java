package org.ow2.sirocco.cloudmanager.model.cimi;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class SystemTemplate extends CloudTemplate {
    private static final long serialVersionUID = 1L;

    private Set<ComponentDescriptor> componentDescriptors;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="system_temp_id")
    public Set<ComponentDescriptor> getComponentDescriptors() {
        return componentDescriptors;
    }

    public void setComponentDescriptors(
            Set<ComponentDescriptor> componentDescriptors) {
        this.componentDescriptors = componentDescriptors;
    }

}
