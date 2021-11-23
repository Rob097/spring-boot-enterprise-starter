package com.rob.uiapi.dto.mappers;

import org.springframework.stereotype.Component;

import com.rob.core.models.SYS.Permission;
import com.rob.core.utils.java.IMapper;
import com.rob.uiapi.dto.models.UserR.RoleR.PermissionR;

@Component
public class PermissionRMapper implements IMapper<Permission, PermissionR>{

	@Override
	public PermissionR map(Permission input) {
		return this.map(input, null);
	}
	
	@Override
	public PermissionR map(Permission input, PermissionR output) {
		if(input==null) {
			return null;
		}
		
		if(output==null) {
			output = new PermissionR();
		}
		
		output.setId(input.getId());
		output.setName(input.getName());
		output.setDescription(input.getDescription());
	
		return output;
		
	}

}
