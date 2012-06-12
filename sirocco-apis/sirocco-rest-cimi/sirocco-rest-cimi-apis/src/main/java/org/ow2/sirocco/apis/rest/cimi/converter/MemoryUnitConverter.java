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

import org.ow2.sirocco.apis.rest.cimi.domain.MemoryUnit;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;
import org.ow2.sirocco.cloudmanager.model.cimi.Memory;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: String via {@link MemoryUnit}</li>
 * <li>Service model: {@link Memory.MemoryUnit}</li>
 * </ul>
 * </p>
 */
public class MemoryUnitConverter implements CimiConverter {
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
                Memory.MemoryUnit service = (Memory.MemoryUnit) dataService;
                switch (service) {
                case BYTE:
                    cimi = MemoryUnit.BYTE.getLabel();
                    break;
                case KIBIBYTE:
                    cimi = MemoryUnit.KibiBYTE.getLabel();
                    break;
                case MEGIBYTE:
                    cimi = MemoryUnit.MebiBYTE.getLabel();
                    break;
                case GIGIBYTE:
                    cimi = MemoryUnit.GibiBYTE.getLabel();
                    break;
                case TEBIBYTE:
                    cimi = MemoryUnit.TebiBYTE.getLabel();
                    break;
                case PETIBYTE:
                    cimi = MemoryUnit.PebiBYTE.getLabel();
                    break;
                case EXBIBYTE:
                    cimi = MemoryUnit.ExbiBYTE.getLabel();
                    break;
                case ZEBIBYTE:
                    cimi = MemoryUnit.ZebiBYTE.getLabel();
                    break;
                case YOBIBYTE:
                    cimi = MemoryUnit.YobiBYTE.getLabel();
                    break;
                default:
                    throw new InvalidConversionException("Unknown MemoryUnit Unit : " + service);
                }
            } catch (ClassCastException e) {
                throw new InvalidConversionException("Unknown MemoryUnit Unit : " + dataService);
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
        Memory.MemoryUnit service = null;
        if (null != dataCimi) {
            MemoryUnit cimi = MemoryUnit.findValueOf((String) dataCimi);
            if (null == cimi) {
                throw new InvalidConversionException("Unknown MemoryUnit Unit : " + dataCimi);
            }
            switch (cimi) {
            case BYTE:
                service = Memory.MemoryUnit.BYTE;
                break;
            case KibiBYTE:
                service = Memory.MemoryUnit.KIBIBYTE;
                break;
            case MebiBYTE:
                service = Memory.MemoryUnit.MEGIBYTE;
                break;
            case GibiBYTE:
                service = Memory.MemoryUnit.GIGIBYTE;
                break;
            case TebiBYTE:
                service = Memory.MemoryUnit.TEBIBYTE;
                break;
            case PebiBYTE:
                service = Memory.MemoryUnit.PETIBYTE;
                break;
            case ExbiBYTE:
                service = Memory.MemoryUnit.EXBIBYTE;
                break;
            case ZebiBYTE:
                service = Memory.MemoryUnit.ZEBIBYTE;
                break;
            case YobiBYTE:
                service = Memory.MemoryUnit.YOBIBYTE;
                break;
            default:
                throw new InvalidConversionException("Unknown MemoryUnit Unit : " + dataCimi);
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
