package com.rob.uiapi.dto.mappers;

import org.springframework.stereotype.Component;

import com.rob.core.models.SYS.Permission;
import com.rob.core.utils.java.IMapper;
import com.rob.uiapi.dto.models.UserR.RoleR.PermissionR;

@Component
public class PermissionMapper implements IMapper<PermissionR, Permission>{

	@Override
	public Permission map(PermissionR input) {
		return this.map(input, null);
	}
	
	@Override
	public Permission map(PermissionR input, Permission output) {
		if(input==null) {
			return null;
		}
		
		if(output==null) {
			output = new Permission();
		}
		
		output.setId(input.getId());
		output.setName(input.getName());
		output.setDescription(input.getDescription());
	
		return output;
		
	}

}
