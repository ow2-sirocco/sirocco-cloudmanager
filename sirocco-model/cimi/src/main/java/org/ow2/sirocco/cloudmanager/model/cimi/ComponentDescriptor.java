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

package org.ow2.sirocco.cloudmanager.model.cimi;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class ComponentDescriptor extends CloudEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static enum ComponentType {
        SYSTEM,VOLUME,MACHINE,CREDENTIALS
    }
    
    private String componentName;
    private String componentDescription;
    private Map<String,Object> componentProperties;
    /**
     * Type of component to be instanciated
     */
    private ComponentType componentType;
    /**
     * Id of template entity to be used to instanciate the component
     */
    private String componentTemplateId;
    private Integer componentQuantity;
    
    private SystemTemplate systemTemplate;
    
    public String getComponentName() {
        return componentName;
    }
    public void setComponentName(String name) {
        this.componentName = name;
    }
    public String getComponentDescription() {
        return componentDescription;
    }
    public void setComponentDescription(String description) {
        this.componentDescription = description;
    }
    @Lob
    public Map<String, Object> getComponentProperties() {
        return componentProperties;
    }
    public void setComponentProperties(Map<String, Object> properties) {
        this.componentProperties = properties;
    }
    public ComponentType getComponentType() {
        return componentType;
    }
    public void setComponentType(ComponentType type) {
        this.componentType = type;
    }
    public String getComponentTemplate() {
        return componentTemplateId;
    }
    public void setComponentTemplate(String componentTemplateId) {
        this.componentTemplateId = componentTemplateId;
    }
    public Integer getComponentQuantity() {
        return componentQuantity;
    }
    public void setComponentQuantity(Integer quantity) {
        this.componentQuantity = quantity;
    }
    
    @ManyToOne
    public SystemTemplate getSystemTemplate() {
        return systemTemplate;
    }
    public void setSystemTemplate(SystemTemplate systemTemplate) {
        this.systemTemplate = systemTemplate;
    }

}