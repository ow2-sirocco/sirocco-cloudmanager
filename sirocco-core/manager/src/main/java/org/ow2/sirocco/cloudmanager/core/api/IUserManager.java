package org.ow2.sirocco.cloudmanager.core.api;

import java.util.Map;

import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.model.cimi.extension.User;

public interface IUserManager {

    static final String EJB_JNDI_NAME = "UserManager";

    User createUser(String firstName, String lastName, String email,
            String username, String password) throws CloudProviderException;

    User createUser(User u) throws CloudProviderException;

    User getUserById(String userId) throws CloudProviderException;

    User getUserByUsername(String userName) throws CloudProviderException;

    User updateUser(User user) throws CloudProviderException;

    User updateUser(String id, Map<String, Object> updatedAttributes)
            throws CloudProviderException;

    void deleteUser(String userId) throws CloudProviderException;
}
