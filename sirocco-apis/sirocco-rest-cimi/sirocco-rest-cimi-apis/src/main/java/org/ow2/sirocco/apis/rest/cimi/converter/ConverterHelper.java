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

import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.VolumeImage;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;

/**
 * 
 */
public class ConverterHelper {

    public static Float toFloat(final Integer value) {
        Float converted = null;
        if (null != value) {
            converted = value.floatValue();
        }
        return converted;
    }

    public static Integer toInteger(final Float value) {
        Integer converted = null;
        if (null != value) {
            converted = value.intValue();
        }
        return converted;
    }

    public static String toString(final Job.Status value) {
        String converted = null;
        if (null != value) {
            converted = value.toString();
        }
        return converted;
    }

    public static String toString(final Machine.State value) {
        String converted = null;
        if (null != value) {
            converted = value.toString();
        }
        return converted;
    }

    public static String toString(final MachineImage.State value) {
        String converted = null;
        if (null != value) {
            converted = value.toString();
        }
        return converted;
    }

    public static String toString(final MachineImage.Type value) {
        String converted = null;
        if (null != value) {
            converted = value.toString();
        }
        return converted;
    }

    public static String toString(final Volume.State value) {
        String converted = null;
        if (null != value) {
            converted = value.toString();
        }
        return converted;
    }

    public static String toString(final VolumeImage.State value) {
        String converted = null;
        if (null != value) {
            converted = value.toString();
        }
        return converted;
    }

    public static String toString(final System.State value) {
        String converted = null;
        if (null != value) {
            converted = value.toString();
        }
        return converted;
    }
}
