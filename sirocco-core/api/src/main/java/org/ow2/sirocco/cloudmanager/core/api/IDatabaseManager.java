package org.ow2.sirocco.cloudmanager.core.api;

public interface IDatabaseManager {
    void cleanup() throws Exception;

    String getUnderlyingDB() throws Exception;
}
