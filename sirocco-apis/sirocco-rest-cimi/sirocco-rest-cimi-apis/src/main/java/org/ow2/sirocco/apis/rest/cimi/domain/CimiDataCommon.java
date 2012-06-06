package org.ow2.sirocco.apis.rest.cimi.domain;

import java.util.Map;

/**
 * Interface with the common accessors of multiple resources.
 */
public interface CimiDataCommon extends CimiData {

    /**
     * Return the value of field "name".
     * 
     * @return The value
     */
    String getName();

    /**
     * Set the value of field "name".
     * 
     * @param name The value
     */
    void setName(final String name);

    /**
     * Return the value of field "description".
     * 
     * @return The value
     */
    String getDescription();

    /**
     * Set the value of field "description".
     * 
     * @param description The value
     */
    void setDescription(final String description);

    /**
     * Return the value of field "properties".
     * 
     * @return The value
     */
    Map<String, String> getProperties();

    /**
     * Set the value of field "properties".
     * 
     * @param properties The value
     */
    void setProperties(final Map<String, String> properties);

}