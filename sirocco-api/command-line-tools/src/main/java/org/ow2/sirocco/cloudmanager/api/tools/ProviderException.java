package org.ow2.sirocco.cloudmanager.api.tools;


public class ProviderException extends Exception {

    public ProviderException(final String message) {
        super(message);
    }

    public ProviderException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
