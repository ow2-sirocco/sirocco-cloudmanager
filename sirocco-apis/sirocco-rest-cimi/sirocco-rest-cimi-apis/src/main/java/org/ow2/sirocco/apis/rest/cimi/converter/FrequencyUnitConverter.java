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
package org.ow2.sirocco.apis.rest.cimi.converter;

import org.ow2.sirocco.apis.rest.cimi.domain.FrequencyUnit;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu;
import org.ow2.sirocco.cloudmanager.model.cimi.Cpu.Frequency;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: String via {@link FrequencyUnit}</li>
 * <li>Service model: {@link Cpu.Frequency}</li>
 * </ul>
 * </p>
 */
public class FrequencyUnitConverter implements CimiConverter {
    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toCimi(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        String cimi = null;
        if (null != dataService) {
            try {
                Frequency service = (Frequency) dataService;
                switch (service) {
                case HERTZ:
                    cimi = FrequencyUnit.HERTZ.getLabel();
                    break;
                case MEGA:
                    cimi = FrequencyUnit.MEGAHERTZ.getLabel();
                    break;
                case GIGA:
                    cimi = FrequencyUnit.GIGAHERTZ.getLabel();
                    break;
                default:
                    throw new InvalidConversionException("Unknown Frequency Unit : " + service);
                }
            } catch (ClassCastException e) {
                throw new InvalidConversionException("Unknown Frequency Unit : " + dataService);
            }
        }
        return cimi;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#toService(org.ow2.sirocco.apis.rest.cimi.utils.CimiContextImpl,
     *      java.lang.Object)
     */
    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        Frequency service = null;
        if (null != dataCimi) {
            FrequencyUnit cimi = FrequencyUnit.findValueOf((String) dataCimi);
            if (null == cimi) {
                throw new InvalidConversionException("Unknown Frequency Unit : " + dataCimi);
            }
            switch (cimi) {
            case HERTZ:
                service = Frequency.HERTZ;
                break;
            case MEGAHERTZ:
                service = Frequency.MEGA;
                break;
            case GIGAHERTZ:
                service = Frequency.GIGA;
                break;
            default:
                throw new InvalidConversionException("Unknown Frequency Unit : " + dataCimi);
            }
        }
        return service;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#copyToCimi(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      java.lang.Object,java.lang.Object)
     */
    @Override
    public void copyToCimi(final CimiContext context, final Object dataService, final Object dataCimi) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.apis.rest.cimi.converter.CimiConverter#copyToService(org.ow2.sirocco.apis.rest.cimi.request.CimiContext,
     *      java.lang.Object,java.lang.Object)
     */
    @Override
    public void copyToService(final CimiContext context, final Object dataCimi, final Object dataService) {
        throw new UnsupportedOperationException();
    }
}
