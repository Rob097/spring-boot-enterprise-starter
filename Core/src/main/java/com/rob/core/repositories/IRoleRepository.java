package com.rob.core.repositories;

import java.sql.SQLException;
import java.util.List;

import com.rob.core.database.RoleSearchCriteria;
import com.rob.core.fetch.modules.Fetch;
import com.rob.core.models.SYS.Role;

public interface IRoleRepository {

	Role findById(Integer id) throws SQLException;

	Role findById(Integer id, Fetch fetch) throws SQLException;
	
	Role findSingleByCriteria(RoleSearchCriteria criteria) throws SQLException;
	
	List<Role> findByCriteria(RoleSearchCriteria criteria) throws SQLException;
	
	Role create(Role data) throws SQLException;
	
	int createPermissionsRelations(int roleId, int permissionId) throws SQLException;
	
	int deleteAllPermissionsRelations(int roleId) throws SQLException;
	
}
