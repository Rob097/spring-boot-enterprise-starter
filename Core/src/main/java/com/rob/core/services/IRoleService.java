package com.rob.core.services;

import java.sql.SQLException;

import com.rob.core.models.SYS.Role;

public interface IRoleService {

	Role create(Role role) throws SQLException;

	Role update(Role role) throws SQLException;
	
}
