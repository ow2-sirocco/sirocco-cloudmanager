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
 *  $Id: IJobListener.java 913 2012-02-20 09:34:20Z ycas7461 $
 *
 */
package org.ow2.sirocco.cloudmanager.core.api;

import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;

/**
 * Job event handling
 */
public interface IJobListener {

    boolean jobCompletionHandler(final String job_id) throws CloudProviderException;

}
