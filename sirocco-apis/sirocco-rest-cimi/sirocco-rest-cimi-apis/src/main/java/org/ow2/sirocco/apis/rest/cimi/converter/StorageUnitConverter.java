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

import org.ow2.sirocco.apis.rest.cimi.domain.StorageUnit;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: String via {@link StorageUnit}</li>
 * <li>Service model:
 * {@link org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit}</li>
 * </ul>
 * </p>
 */
@Deprecated
public class StorageUnitConverter implements CimiConverter {
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
            // try {
            // org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit service =
            // (org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit)
            // dataService;
            // switch (service) {
            // case BYTE:
            // cimi = StorageUnit.BYTE.getLabel();
            // break;
            // case KILOBYTE:
            // cimi = StorageUnit.KILOBYTE.getLabel();
            // break;
            // case MEGABYTE:
            // cimi = StorageUnit.MEGABYTE.getLabel();
            // break;
            // case GIGABYTE:
            // cimi = StorageUnit.GIGABYTE.getLabel();
            // break;
            // case TERABYTE:
            // cimi = StorageUnit.TERABYTE.getLabel();
            // break;
            // case PETABYTE:
            // cimi = StorageUnit.PETABYTE.getLabel();
            // break;
            // case EXABYTE:
            // cimi = StorageUnit.EXABYTE.getLabel();
            // break;
            // case ZETTABYTE:
            // cimi = StorageUnit.ZETTABYTE.getLabel();
            // break;
            // case YOTTABYTE:
            // cimi = StorageUnit.YOTTABYTE.getLabel();
            // break;
            // default:
            // throw new
            // InvalidConversionException("Unknown StorageUnit Unit : " +
            // service);
            // }
            // } catch (ClassCastException e) {
            // throw new
            // InvalidConversionException("Unknown StorageUnit Unit : " +
            // dataService);
            // }
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
        // org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit service = null;
        // if (null != dataCimi) {
        // StorageUnit cimi = StorageUnit.findValueOf((String) dataCimi);
        // if (null == cimi) {
        // throw new InvalidConversionException("Unknown StorageUnit Unit : " +
        // dataCimi);
        // }
        // switch (cimi) {
        // case BYTE:
        // service = org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.BYTE;
        // break;
        // case KILOBYTE:
        // service =
        // org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.KILOBYTE;
        // break;
        // case MEGABYTE:
        // service =
        // org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.MEGABYTE;
        // break;
        // case GIGABYTE:
        // service =
        // org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.GIGABYTE;
        // break;
        // case TERABYTE:
        // service =
        // org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.TERABYTE;
        // break;
        // case PETABYTE:
        // service =
        // org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.PETABYTE;
        // break;
        // case EXABYTE:
        // service =
        // org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.EXABYTE;
        // break;
        // case ZETTABYTE:
        // service =
        // org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.ZETTABYTE;
        // break;
        // case YOTTABYTE:
        // service =
        // org.ow2.sirocco.cloudmanager.model.cimi.StorageUnit.YOTTABYTE;
        // break;
        // default:
        // throw new InvalidConversionException("Unknown StorageUnit Unit : " +
        // dataCimi);
        // }
        // }
        // return service;
        return null;
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
