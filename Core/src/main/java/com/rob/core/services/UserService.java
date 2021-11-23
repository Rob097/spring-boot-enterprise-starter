package com.rob.core.services;

import java.sql.SQLException;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rob.core.database.UserSearchCriteria;
import com.rob.core.fetch.UserFetchHandler;
import com.rob.core.fetch.modules.FetchBuilder;
import com.rob.core.models.SYS.Role;
import com.rob.core.models.SYS.User;
import com.rob.core.repositories.IUserRepository;

@Service
public class UserService implements IUserService, UserDetailsService {

	@Autowired
    private IUserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Validate.notNull(username, "Mandatory paramete is missing: username");
		
		UserSearchCriteria criteria = new UserSearchCriteria();
		FetchBuilder fetchBuilder = new FetchBuilder();
		fetchBuilder.addOption(UserFetchHandler.FETCH_ROLES);
		criteria.setFetch(fetchBuilder.build());
		criteria.setUsername(username);
		
		try {
			User user = userRepository.findSingleByCriteria(criteria);
			Validate.notNull(user, "No User was found with username: " + username);
			
			return user;
		} catch (SQLException e) {
			throw new RuntimeException("Error loading user by username",e);
		}
	}

	@Override
	public User create(User user) throws SQLException {
		Validate.notNull(user, "Mandatory paramete is missing: user");

		userRepository.create(user);
		
		User u = (User) this.loadUserByUsername(user.getUsername());
		if(u!=null) {
			user.setId(u.getId());
		}
		
		if(user.getRoles()!=null && !user.getRoles().isEmpty() && user.getId()!=null) {
			for (Role role : user.getRoles()) {
				userRepository.createRoleRelation(user.getId(), role.getId());
			}
		}

		Validate.notNull(user, "Error creating new user.");
		
		return user;
		
	}
	
	@Override
	public User update(User user) throws SQLException {
		Validate.notNull(user, "Mandatory paramete is missing: user");
		Validate.notNull(user.getId(), "Mandatory paramete is missing: Id");

		userRepository.update(user);		

		Validate.notNull(user, "Error updating new user.");
		
		return user;
		
	}

}
