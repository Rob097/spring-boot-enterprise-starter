package com.rob.core.repositories;

import java.sql.SQLException;
import java.util.List;

import com.rob.core.database.PermissionSearchCriteria;
import com.rob.core.models.SYS.Permission;

public interface IPermissionRepository {

	Permission findById(int id) throws SQLException;
	
	Permission findSingleByCriteria(PermissionSearchCriteria criteria) throws SQLException;
	
	List<Permission> findByCriteria(PermissionSearchCriteria criteria) throws SQLException;
	
	Permission create(Permission data) throws SQLException;

	Permission update(Permission data) throws SQLException;
	
}
