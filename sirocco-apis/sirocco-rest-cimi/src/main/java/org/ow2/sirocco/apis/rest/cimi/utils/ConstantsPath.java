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
package org.ow2.sirocco.apis.rest.cimi.utils;

public abstract class ConstantsPath {

    public static final String CLOUDENTRYPOINT = "";

    public static final String CLOUDENTRYPOINT_PATH = "/" + ConstantsPath.CLOUDENTRYPOINT;

    public static final String MACHINE = "machines";

    public static final String MACHINE_PATH = "/" + ConstantsPath.MACHINE;

    public static final String MACHINE_IMAGE = "machineImages";

    public static final String MACHINE_IMAGE_PATH = "/" + ConstantsPath.MACHINE_IMAGE;

    public static final String MACHINE_IMAGE_LOCATION = "machineImagesData";

    public static final String MACHINE_IMAGE_LOCATION_PATH = "/" + ConstantsPath.MACHINE_IMAGE_LOCATION;

    public static final String MACHINE_CONFIGURATION = "machineConfigs";

    public static final String MACHINE_CONFIGURATION_PATH = "/" + ConstantsPath.MACHINE_CONFIGURATION;

    public static final String CREDENTIALS = "credentials";

    public static final String CREDENTIALS_PATH = "/" + ConstantsPath.MACHINE_CONFIGURATION;

    public static final String MACHINE_TEMPLATE = "machinesTemplates";

    public static final String MACHINE_TEMPLATE_PATH = "/" + ConstantsPath.MACHINE_TEMPLATE;

    public static final String CREDENTIALS_TEMPLATE = "credentialsTemplates";

    public static final String CREDENTIALS_TEMPLATE_PATH = "/" + ConstantsPath.CREDENTIALS_TEMPLATE;

    public static final String JOB = "jobs";

    public static final String JOB_PATH = "/" + ConstantsPath.JOB;
}
