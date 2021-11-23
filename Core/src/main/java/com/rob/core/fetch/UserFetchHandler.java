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

import com.rob.core.database.RoleSearchCriteria;
import com.rob.core.fetch.modules.AbstractFetchHandler;
import com.rob.core.fetch.modules.Fetch;
import com.rob.core.fetch.modules.Fetch.Mapping;
import com.rob.core.fetch.modules.Fetch.Option;
import com.rob.core.fetch.modules.Fetch.Strategy;
import com.rob.core.models.SYS.Role;
import com.rob.core.models.SYS.User;
import com.rob.core.fetch.modules.FetchBuilder;
import com.rob.core.repositories.IRoleRepository;

@Component
public class UserFetchHandler extends AbstractFetchHandler<User>{

	@Autowired
	private ApplicationContext applicationContext;
	
	public static final String FETCH_ROLES = "roles";
	
	public static final String FETCH_ROLES_PERMISSIONS = FETCH_ROLES + "." + RoleFetchHandler.FETCH_PERMISSIONS;
	
	private static final Set<String> supportedKeys = new HashSet<>(Arrays.asList(
			FETCH_ROLES
	));
	
	@Override
	public User handle(User entity, Fetch fetch, ResultSet resultSet, Map<String, User> groupingMap)
			throws SQLException {
		if (entity == null) {
			return null;
		}
		
		if (fetch == null) {
			return entity;
		}
		
		if(fetch.hasOption(FETCH_ROLES)) {
			Fetch.Option fetchOption = fetch.getOption(FETCH_ROLES);
			validate(fetchOption);	
			
			if (entity.getId()!=null) {
				IRoleRepository roleRepository = applicationContext.getBean(IRoleRepository.class);
				RoleSearchCriteria criteria = new RoleSearchCriteria(); 
				criteria.setUserId(entity.getId());
				criteria.setFetch(FetchBuilder.child(fetch, FETCH_ROLES));
				List<Role> roles = roleRepository.findByCriteria(criteria);
				if (roles != null) {
					entity.setRoles(roles);
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
		if (option.getKey().equals(FETCH_ROLES)) {
			return option.getStrategy() == Strategy.REPOSITORY;
		}
		return false;
	}

	@Override
	protected boolean supportNonDefaultMapping(Option option) {
		if (option.getKey().equals(FETCH_ROLES)) {
			return option.getMapping() == Mapping.DEFAULT;
		}
		return false;
	}

}
