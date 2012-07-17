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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * $Id$
 *
 */
package org.ow2.sirocco.apis.rest.cimi.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to manage CIMI Select expression in the QueryString.
 */
public class CimiSelect extends CimiExpand {

    private static final long serialVersionUID = 1L;

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(CimiSelect.class);

    /**
     * Default constructor.
     */
    public CimiSelect() {
        super();
    }

    /**
     * Set constructor.
     * 
     * @param initialValue The initial value
     */
    public CimiSelect(final List<String> initialValues) {
        super(initialValues);
    }

    /**
     * Set constructor.
     * 
     * @param initialValues The initial values
     */
    public CimiSelect(final String initialValue) {
        super(initialValue);
    }

    /**
     * Set constructor.
     * 
     * @param initialValues The initial values
     */
    public CimiSelect(final String[] initialValues) {
        super(initialValues);
    }

    /**
     * {@inheritDoc}
     * <p>
     * If WildCard exists, remove all values.
     * </p>
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.request.CimiParam#prepare()
     */
    @Override
    protected void prepare() {
        super.prepare();
        if (true == this.hasAll()) {
            this.setValues(null);
        }
    }

    /**
     * Copy the values of bean in the map with the attributes found in the
     * CimiSelect.
     * <p>
     * If a attribute name is not found in bean, it is not copied in map.
     * </p>
     * 
     * @param bean The bean where are the values
     * @return A map with the attribute name and his value
     */
    public Map<String, Object> copyBeanAttributes(final Object bean) {
        Map<String, Object> attrValues = new HashMap<String, Object>();
        Object value;
        if (false == this.isEmpty()) {
            for (String name : this.getValues()) {
                try {
                    value = PropertyUtils.getSimpleProperty(bean, name);
                    if (null != value) {
                        attrValues.put(name, value);
                    }
                } catch (Exception e) {
                    CimiSelect.LOGGER.debug("Property [{}] not found in bean [{}] with this message error: {}", new Object[] {
                        name, bean.getClass().getName(), e.getMessage()});
                }
            }
        }
        return attrValues;
    }

}
