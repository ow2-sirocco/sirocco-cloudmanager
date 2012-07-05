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

    public static final String CIMI_XML_NAMESPACE = "http://schemas.dmtf.org/cimi/1";

    public static final String ADDRESS = "addresses";

    public static final String ADDRESS_PATH = "/" + ConstantsPath.ADDRESS;

    public static final String ADDRESS_TEMPLATE = "addressTemplates";

    public static final String ADDRESS_TEMPLATE_PATH = "/" + ConstantsPath.ADDRESS_TEMPLATE;

    public static final String CLOUDENTRYPOINT = "";

    public static final String CLOUDENTRYPOINT_PATH = "/" + ConstantsPath.CLOUDENTRYPOINT;

    public static final String CREDENTIAL = "credentials";

    public static final String CREDENTIAL_PATH = "/" + ConstantsPath.CREDENTIAL;

    public static final String CREDENTIAL_TEMPLATE = "credentialTemplates";

    public static final String CREDENTIAL_TEMPLATE_PATH = "/" + ConstantsPath.CREDENTIAL_TEMPLATE;

    public static final String DISK = "disks";

    public static final String DISK_PATH = "/" + ConstantsPath.DISK;

    public static final String JOB = "jobs";

    public static final String JOB_PATH = "/" + ConstantsPath.JOB;

    public static final String MACHINE = "machines";

    public static final String MACHINE_PATH = "/" + ConstantsPath.MACHINE;

    public static final String MACHINE_TEMPLATE = "machineTemplates";

    public static final String MACHINE_TEMPLATE_PATH = "/" + ConstantsPath.MACHINE_TEMPLATE;

    public static final String MACHINE_IMAGE = "machineImages";

    public static final String MACHINE_IMAGE_PATH = "/" + ConstantsPath.MACHINE_IMAGE;

    public static final String MACHINE_CONFIGURATION = "machineConfigs";

    public static final String MACHINE_CONFIGURATION_PATH = "/" + ConstantsPath.MACHINE_CONFIGURATION;

    public static final String NETWORK = "networks";

    public static final String NETWORK_PATH = "/" + ConstantsPath.NETWORK;

    public static final String NETWORK_TEMPLATE = "networkTemplates";

    public static final String NETWORK_TEMPLATE_PATH = "/" + ConstantsPath.NETWORK_TEMPLATE;

    public static final String SYSTEM = "systems";

    public static final String SYSTEM_PATH = "/" + ConstantsPath.SYSTEM;

    public static final String SYSTEM_TEMPLATE = "systemTemplates";

    public static final String SYSTEM_TEMPLATE_PATH = "/" + ConstantsPath.SYSTEM_TEMPLATE;

    public static final String VOLUME = "volumes";

    public static final String VOLUME_PATH = "/" + ConstantsPath.VOLUME;

    public static final String VOLUME_IMAGE = "volumeImages";

    public static final String VOLUME_IMAGE_PATH = "/" + ConstantsPath.VOLUME_IMAGE;

    public static final String VOLUME_CONFIGURATION = "volumeConfigs";

    public static final String VOLUME_CONFIGURATION_PATH = "/" + ConstantsPath.VOLUME_CONFIGURATION;

    public static final String VOLUME_TEMPLATE = "volumeTemplates";

    public static final String VOLUME_TEMPLATE_PATH = "/" + ConstantsPath.VOLUME_TEMPLATE;
}
