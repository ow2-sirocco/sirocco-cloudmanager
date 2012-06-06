package org.ow2.sirocco.apis.rest.cimi.domain;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.ow2.sirocco.apis.rest.cimi.utils.CimiDateAdapter;

public interface CimiObjectCommon extends CimiObject, CimiDataCommon {

    /**
     * Return the value of field "created".
     * 
     * @return The value
     */
    @XmlJavaTypeAdapter(CimiDateAdapter.class)
    Date getCreated();

    /**
     * Set the value of field "created".
     * 
     * @param created The value
     */
    void setCreated(final Date created);

    /**
     * Return the value of field "updated".
     * 
     * @return The value
     */
    @XmlJavaTypeAdapter(CimiDateAdapter.class)
    Date getUpdated();

    /**
     * Set the value of field "updated".
     * 
     * @param updated The value
     */
    void setUpdated(final Date updated);

}