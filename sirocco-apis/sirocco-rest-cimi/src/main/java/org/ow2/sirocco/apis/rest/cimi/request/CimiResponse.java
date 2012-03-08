package org.ow2.sirocco.apis.rest.cimi.request;

import javax.ws.rs.core.Response.Status;

import org.ow2.sirocco.apis.rest.cimi.domain.CimiData;

public class CimiResponse {

    private CimiData cimiData;

    private String messageError;

    private Status status = Status.OK;

    public String getMessageError() {
        return messageError;
    }

    public void setMessageError(String messageError) {
        this.messageError = messageError;
    }

    public CimiData getCimiData() {
        return cimiData;
    }

    public void setCimiData(CimiData cimiData) {
        this.cimiData = cimiData;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
