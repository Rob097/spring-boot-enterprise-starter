package com.rob.core.fetch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.rob.core.database.PermissionSearchCriteria;
import com.rob.core.fetch.modules.AbstractFetchHandler;
import com.rob.core.fetch.modules.Fetch;
import com.rob.core.fetch.modules.Fetch.Mapping;
import com.rob.core.fetch.modules.Fetch.Option;
import com.rob.core.fetch.modules.Fetch.Strategy;
import com.rob.core.models.SYS.Permission;
import com.rob.core.models.SYS.Role;
import com.rob.core.repositories.IPermissionRepository;

@Component
public class RoleFetchHandler extends AbstractFetchHandler<Role>{

	@Autowired
	private ApplicationContext applicationContext;
	
	public static final String FETCH_PERMISSIONS = "permissions";
	
	private static final Set<String> supportedKeys = new HashSet<>(Arrays.asList(
			FETCH_PERMISSIONS
	));
	
	@Override
	public Role handle(Role entity, Fetch fetch, ResultSet resultSet, Map<String, Role> groupingMap)
			throws SQLException {
		if (entity == null) {
			return null;
		}
		
		if (fetch == null) {
			return entity;
		}
		
		if(fetch.hasOption(FETCH_PERMISSIONS)) {
			Fetch.Option fetchOption = fetch.getOption(FETCH_PERMISSIONS);
			validate(fetchOption);	
			
			if (entity.getId()!=null) {
				IPermissionRepository permissionRepository = applicationContext.getBean(IPermissionRepository.class);
				PermissionSearchCriteria criteria = new PermissionSearchCriteria(); 
				criteria.setRoleId(entity.getId());
				List<Permission> permissions = permissionRepository.findByCriteria(criteria);
				if (permissions != null) {
					entity.setPermissions(permissions);
				}
			}
		}
		
		return entity;
	}

	@Override
	public boolean supportKey(Option option) {
		return supportedKeys.contains(option.getKey());
	}

	@Override
	protected boolean supportNonDefaultStrategy(Option option) {
		if (option.getKey().equals(FETCH_PERMISSIONS)) {
			return option.getStrategy() == Strategy.REPOSITORY;
		}
		return false;
	}

	@Override
	protected boolean supportNonDefaultMapping(Option option) {
		if (option.getKey().equals(FETCH_PERMISSIONS)) {
			return option.getMapping() == Mapping.DEFAULT;
		}
		return false;
	}

}
