package com.rob.core.repositories;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import com.rob.core.database.RoleManagerQuery;
import com.rob.core.database.RoleSearchCriteria;
import com.rob.core.fetch.RoleFetchHandler;
import com.rob.core.fetch.modules.Fetch;
import com.rob.core.fetch.modules.FetchBuilder;
import com.rob.core.models.SYS.Role;
import com.rob.core.utils.db.PreparedStatementBuilder;

@Repository
public class RoleRepository implements IRoleRepository {

	@Autowired
	private DataSource dataSource;

	private final RoleManagerQuery queryFactory;
	
	@Autowired
	private RoleFetchHandler fetchHandler;

	public RoleRepository() {
		queryFactory = new RoleManagerQuery();
	}
	
	@Override
	public Role findById(Integer id) throws SQLException {
		Validate.notNull(id, "Parametro obbligatorio mancante: identificativo ruolo.");

		return this.findById(id, FetchBuilder.none());
	}
	
	@Override
	public Role findById(Integer id, Fetch fetch) throws SQLException {
		Validate.notNull(id, "Parametro obbligatorio mancante: identificativo utente.");
		
		RoleSearchCriteria criteria = new RoleSearchCriteria();
		criteria.setId(id);
		criteria.setFetch(fetch);
		
		return this.findSingleByCriteria(criteria);
	}

	@Override
	public Role findSingleByCriteria(RoleSearchCriteria criteria) throws SQLException {
		Validate.notNull(criteria, "Parametro obbligatorio mancante.");
		if(criteria.getRange() == null) {
			criteria.setMaxRows(1);
		}
		List<Role> results = this.findByCriteria(criteria);
		if (results != null && !results.isEmpty()) {
			return results.iterator().next();
		}
		
		return null;
	}

	@Override
	public List<Role> findByCriteria(RoleSearchCriteria criteria) throws SQLException {
		Validate.notNull(criteria, "Parametro obbligatorio mancante.");
		List<Role> results = new ArrayList<>();
		Fetch fetch = criteria.getFetch();
		
		try(
			PreparedStatementBuilder bld = queryFactory.sqlFindByCriteria(criteria);
			Connection conn = DataSourceUtils.getConnection(dataSource);
			ResultSet rst = bld.executeQuery(conn);
		){
			
			while(rst.next()) {
				Role role = new Role(rst);
				fetchHandler.handle(role, fetch, rst);
				results.add(role);
			}	
		}
		
		return results;
	}

	@Override
	public Role create(Role data) throws SQLException {
		Validate.notNull(data, "Oggetto non valido.");
		Validate.notNull(data.getId(), "Parametro obbligatorio mancante: Identificativo ruolo");
		
		/*String id = this.sysModuleService.getCounterMaster(SYSCounterEnum.LOG_ID.getId(), 100);
		data.setId(id);*/		
		
		try 
		(PreparedStatementBuilder psb = this.queryFactory.sqlCreate(data)) 
		{
			Connection con = DataSourceUtils.getConnection(dataSource);
			psb.executeUpdate(con);
		}
		
		return data;
	}
	
	@Override
	public int createPermissionsRelations(int roleId, int permissionId) throws SQLException {
		
		int created = 0;
		try 
		(PreparedStatementBuilder psb = this.queryFactory.sqlCreatePermissionsRelations(roleId, permissionId)) 
		{
			Connection con = DataSourceUtils.getConnection(dataSource);
			created = psb.executeUpdate(con);
		}
		
		return created;
	}
	
	@Override
	public int deleteAllPermissionsRelations(int roleId) throws SQLException {
		
		int created = 0;
		try 
		(PreparedStatementBuilder psb = this.queryFactory.sqlDeleteAllPermissionsRelations(roleId)) 
		{
			Connection con = DataSourceUtils.getConnection(dataSource);
			created = psb.executeUpdate(con);
		}
		
		return created;
	}

}
