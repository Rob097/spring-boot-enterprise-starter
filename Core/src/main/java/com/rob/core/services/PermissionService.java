package com.rob.core.services;

import java.sql.SQLException;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rob.core.models.SYS.Permission;
import com.rob.core.repositories.IPermissionRepository;

@Service
public class PermissionService implements IPermissionService {
	
	@Autowired
    private IPermissionRepository permissionRepository;

	@Override
	public Permission create(Permission permission) throws SQLException {
		Validate.notNull(permission, "Mandatory paramete is missing: permission");
		Validate.notNull(permission.getId(), "L'identificativo è un campo obbligatorio.");
		
		Permission actual = permissionRepository.findById(permission.getId());
		Validate.isTrue(actual==null, "Esiste già un privilegio con id + " + permission.getId());

		permission = permissionRepository.create(permission);

		Validate.notNull(permission, "Error creating new permission.");
		
		return permission;
	}

	@Override
	public Permission update(Permission permission) throws SQLException {
		Validate.notNull(permission, "Mandatory paramete is missing: permission");
		Validate.notNull(permission.getId(), "L'identificativo è un campo obbligatorio.");
		
		Permission actual = permissionRepository.findById(permission.getId());
		Validate.isTrue(actual!=null, "Non esiste nessun privilegio con id + " + permission.getId());

		permission = permissionRepository.update(permission);

		Validate.notNull(permission, "Error updating a permission.");
		
		return permission;
	}

}
