package com.rob.uiapi.dto.mappers;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rob.core.models.SYS.Role;
import com.rob.core.utils.java.IMapper;
import com.rob.uiapi.dto.models.UserR.RoleR;

@Component
public class RoleMapper  implements IMapper<RoleR, Role>{

	@Autowired
	private PermissionMapper permissionMapper;
	
	@Override
	public Role map(RoleR input) {
		return this.map(input, null);
	}
	
	@Override
	public Role map(RoleR input, Role output) {
		if(input==null) {
			return null;
		}
		
		if(output==null) {
			output = new Role();
		}
		
		output.setId(input.getId());
		output.setName(input.getName());
		if(input.getPermissions()!=null && !input.getPermissions().isEmpty()) {
			output.setPermissions(input.getPermissions().stream().map(permission -> permissionMapper.map(permission)).collect(Collectors.toList()));
		}
	
		return output;
		
	}

}
