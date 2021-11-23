package com.rob.security.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public interface IAuthenticationUserService {
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
