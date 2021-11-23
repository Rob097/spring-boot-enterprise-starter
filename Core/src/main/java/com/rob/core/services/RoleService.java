package com.rob.core.services;

import java.sql.SQLException;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rob.core.models.SYS.Permission;
import com.rob.core.models.SYS.Role;
import com.rob.core.repositories.IRoleRepository;

@Service
public class RoleService implements IRoleService {

	@Autowired
    private IRoleRepository roleRepository;
	
	@Override
	public Role create(Role role) throws SQLException {
		Validate.notNull(role, "Mandatory paramete is missing: role");
		Validate.notNull(role.getId(), "Parametro obbligatorio mancante: Identificativo ruolo");

		role = roleRepository.create(role);
		
		if(role.getPermissions()!=null && !role.getPermissions().isEmpty()) {
			for(Permission permission : role.getPermissions()) {
				roleRepository.createPermissionsRelations(role.getId(), permission.getId());
			}
		}

		Validate.notNull(role, "Error creating new role.");
		
		return role;
	}

	@Override
	public Role update(Role role) throws SQLException {
		Validate.notNull(role, "Mandatory paramete is missing: role");
		Validate.notNull(role.getId(), "Mandatory paramete is missing: id");

		roleRepository.deleteAllPermissionsRelations(role.getId());
		
		if(role.getPermissions()!=null && !role.getPermissions().isEmpty()) {
			for(Permission permission : role.getPermissions()) {
				roleRepository.createPermissionsRelations(role.getId(), permission.getId());
			}
		}

		Validate.notNull(role, "Error creating new role.");
		
		return role;
	}

}
