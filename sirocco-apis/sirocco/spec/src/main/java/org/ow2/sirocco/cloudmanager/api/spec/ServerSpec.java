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

package org.ow2.sirocco.cloudmanager.api.spec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "ServerSpec")
public class ServerSpec {

    private String cloudProviderAccountId;

    private String projectId;

    private String name;

    private String size;

    private Integer imageId;

    private String reservationId;

    private HashMap<String, String> userData;

    public ServerSpec() {
    }

    public String getCloudProviderAccountId() {
        return this.cloudProviderAccountId;
    }

    public void setCloudProviderAccountId(final String cloudProviderAccountId) {
        this.cloudProviderAccountId = cloudProviderAccountId;
    }

    public void setProjectId(final String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(final String size) {
        this.size = size;
    }

    @XmlJavaTypeAdapter(MyMapAdapter.class)
    public HashMap<String, String> getUserData() {
        return this.userData;
    }

    public void setUserData(final HashMap<String, String> userData) {
        this.userData = userData;
    }

    private static class MyMapAdapter extends XmlAdapter<Temp, Map<String, String>> {
        @Override
        public Temp marshal(final Map<String, String> v) throws Exception {
            Temp result = new Temp();
            for (String key : v.keySet()) {
                Item item = new Item();
                item.key = key;
                item.value = v.get(key);
                result.entry.add(item);
            }
            return result;
        }

        @Override
        public Map<String, String> unmarshal(final Temp v) throws Exception {
            HashMap<String, String> result = new HashMap<String, String>();
            for (Item item : v.entry) {
                if (item.key != null && item.value != null) {
                    result.put(item.key, item.value);
                }
            }
            return result;
        }
    }

    private static class Temp {
        @XmlElement
        private List<Item> entry = new ArrayList<Item>();
    }

    private static class Item {
        @XmlAttribute(required = true)
        private String key;

        @XmlAttribute(required = true)
        private String value;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public final String getReservationId() {
        return this.reservationId;
    }

    public final void setReservationId(final String reservationId) {
        this.reservationId = reservationId;
    }

    public void setImageId(final Integer imageId) {
        this.imageId = imageId;
    }

    public Integer getImageId() {
        return this.imageId;
    }

}
