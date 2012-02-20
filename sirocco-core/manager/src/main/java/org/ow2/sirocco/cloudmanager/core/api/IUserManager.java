package org.ow2.sirocco.cloudmanager.core.api;

import org.ow2.sirocco.cloudmanager.core.exception.UserException;
import org.ow2.sirocco.cloudmanager.model.cimi.User;

public interface IUserManager {
	
	static final String EJB_JNDI_NAME = "UserManager";
	
	User createUser(String firstName,String lastName,String email,String username,String password,String publicKey) throws UserException;
	User getUserById(String userId) throws UserException;
	User getUserByUsername(String userName) throws UserException;
	User updateUser(User user) throws UserException;
	void deleteUser(String userId) throws UserException;

}
