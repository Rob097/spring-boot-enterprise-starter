package com.rob.uiapi.dto.mappers;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rob.core.models.SYS.Role;
import com.rob.core.utils.java.IMapper;
import com.rob.uiapi.dto.models.UserR.RoleR;

@Component
public class RoleRMapper implements IMapper<Role, RoleR>{
	
	@Autowired
	private PermissionRMapper permissionRMapper;

	@Override
	public RoleR map(Role input) {
		return this.map(input, null);
	}
	
	@Override
	public RoleR map(Role input, RoleR output) {
		if(input==null) {
			return null;
		}
		
		if(output==null) {
			output = new RoleR();
		}
		
		output.setId(input.getId());
		output.setName(input.getName());
		if(input.getPermissions()!=null && !input.getPermissions().isEmpty()) {
			output.setPermissions(input.getPermissions().stream().map(permission -> permissionRMapper.map(permission)).collect(Collectors.toSet()));
		}
	
		return output;
		
	}

}
