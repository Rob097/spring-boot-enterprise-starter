package com.rob.uiapi.dto.mappers;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rob.core.models.SYS.User;
import com.rob.core.utils.java.IMapper;
import com.rob.uiapi.dto.models.UserR;

@Component
public class UserRMapper implements IMapper<User, UserR>{

	@Autowired
	private RoleRMapper roleRMapper;
	
	@Override
	public UserR map(User input) {
		return this.map(input, null);
	}
	
	@Override
	public UserR map(User input, UserR output) {
		if(input==null) {
			return null;
		}
		
		if(output==null) {
			output = new UserR();
		}
		
		output.setId(input.getId());
		output.setName(input.getName());
		output.setSurname(input.getSurname());
		output.setAge(input.getAge());
		output.setSex(input.getSex());
		output.setUsername(input.getUsername());
		output.setEmail(input.getEmail());
		output.setPassword(input.getPassword());
		output.setAddress(input.getAddress());		
		if(input.getRoles()!=null && !input.getRoles().isEmpty()) {
			output.setRoles(input.getRoles().stream().map(role -> roleRMapper.map(role)).collect(Collectors.toSet()));
		}
		
		return output;
		
	}
	
}
