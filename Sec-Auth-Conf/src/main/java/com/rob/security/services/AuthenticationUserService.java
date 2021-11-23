package com.rob.security.services;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rob.core.services.IUserService;


/**
 * @author Roberto97
 * Service used to manage users in signin and signup
 */
@Service
public class AuthenticationUserService implements IAuthenticationUserService, UserDetailsService {

	@Autowired
    private IUserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Validate.notNull(username, "Username obbligatorio per fare il login.");
		
		UserDetails user = userService.loadUserByUsername(username);
		
		Validate.notNull(user, "Utente non trovato con l'username inserito.");
		
		return user;
		
	}

}
