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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "error")
public class ErrorReport {
    private String errorCode;

    private String errorMessage;

    public ErrorReport() {
    }

    public ErrorReport(final ErrorCode errorCode, final String errorMessage) {
        this.errorCode = errorCode.code;
        this.errorMessage = errorMessage;
    }

    public String getCode() {
        return this.errorCode;
    }

    public void setCode(final String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return this.errorMessage;
    }

    public void setMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static enum ErrorCode {
        AUTH_FAILURE("FC_0"), RESOURCE_QUOTA_EXCEEDED("FC_1"), PUBLIC_IP_ADRESS_LIMIT_EXCEEDED("FC_2"), INVALID_VM_IMAGE_ID(
            "FC_3"), INVALID_VM_INSTANCE_ID("FC_4"), INVALID_VM_CLASS("FC_5"), DUPLICATE_VM_NAME("FC_6"), INVALID_VM_NAME(
            "FC_7"), SERVICE_UNAVAILABLE("FC_8"), INTERNAL_ERROR("FC_9"), INVALID_PROJECT_ID("FC_10"), PERMISSION_DENIED(
            "FC_11"), INVALID_USERNAME("FC_12"), INVALID_POWER_STATE("FC_18"), USERNAME_ALREADY_EXISTS("FC_13"), RESERVATION_IMPOSSIBLE(
            "FC_14"), DESTRUCTION_IMPOSSIBLE("FC_15"), INVALID_ARGUMENT("FC_16"), INSUFFICIENT_RESOURCE("FC_17"), NO_RELEVANT_RESOURCES_POOL(
            "FC_18"), INVALID_VOLUME_ID("FC_19"), VOLUME_IN_USE("FC_20"), INVALID_VM_STATE("FC_21"), CLOUDPROVIDER_IN_USE(
            "FC_22"), CLOUDPROVIDER_ACCOUNT_IN_USE("FC_23");

        /**
         * The code corresponding to the exception
         */
        private final String code;

        /**
         * @return the code of enum variable
         */

        public String getCode() {
            return this.code;
        }

        /**
         * the constructor
         * 
         * @param code the code corresponding to the enum variable.
         */

        private ErrorCode(final String code) {
            this.code = code;
        }

    }

}
