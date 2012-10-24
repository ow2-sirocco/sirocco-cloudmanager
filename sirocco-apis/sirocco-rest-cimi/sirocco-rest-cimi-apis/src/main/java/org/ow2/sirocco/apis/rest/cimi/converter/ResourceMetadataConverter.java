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

import org.ow2.sirocco.apis.rest.cimi.domain.CimiResourceMetadata;
import org.ow2.sirocco.apis.rest.cimi.request.CimiContext;

/**
 * Convert the data of the CIMI model and the service model in both directions.
 * <p>
 * Converted classes:
 * <ul>
 * <li>CIMI model: {@link CimiResourceMetadata}</li>
 * <li>Service model: TODO</li>
 * </ul>
 * </p>
 */
public class ResourceMetadataConverter extends ObjectCommonConverter {

    @Override
    public Object toCimi(final CimiContext context, final Object dataService) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void copyToCimi(final CimiContext context, final Object dataService, final Object dataCimi) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object toService(final CimiContext context, final Object dataCimi) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void copyToService(final CimiContext context, final Object dataCimi, final Object dataService) {
        // TODO Auto-generated method stub

    }

}
