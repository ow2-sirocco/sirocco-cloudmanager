package org.ow2.sirocco.cloudmanager.core.api;

/**
 * Low-level database access
 */
public interface IDatabaseManager {
    void cleanup() throws Exception;

    String getUnderlyingDB() throws Exception;
}
