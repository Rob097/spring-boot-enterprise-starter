package com.rob.core.services;

import java.sql.SQLException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.rob.core.models.SYS.User;


public interface IUserService {	
	
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
	
	User create(User user) throws SQLException;
	
	User update(User user) throws SQLException;
	
}
