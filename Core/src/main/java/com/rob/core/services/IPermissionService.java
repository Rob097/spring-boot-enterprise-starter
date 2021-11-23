package com.rob.core.services;

import java.sql.SQLException;

import com.rob.core.models.SYS.Permission;

public interface IPermissionService {

	Permission create(Permission permission) throws SQLException;
	
	Permission update(Permission permission) throws SQLException;
	
}
