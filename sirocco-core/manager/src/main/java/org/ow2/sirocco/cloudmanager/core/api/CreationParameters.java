
package org.ow2.sirocco.cloudmanager.core.api;


import java.util.Map;

public class CreationParameters {

	private String				name;
	private String				description;
	private Map<String, String>	properties;

	public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }
}
