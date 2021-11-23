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

import com.rob.core.database.PermissionManagerQuery;
import com.rob.core.database.PermissionSearchCriteria;
import com.rob.core.models.SYS.Permission;
import com.rob.core.utils.db.PreparedStatementBuilder;

@Repository
public class PermissionRepository implements IPermissionRepository {

	@Autowired
	private DataSource dataSource;

	private final PermissionManagerQuery queryFactory;
	
	public PermissionRepository() {
		queryFactory = new PermissionManagerQuery();
	}
	
	@Override
	public Permission findById(int id) throws SQLException {
		Validate.notNull(id, "Parametro obbligatorio mancante: identificativo utente.");
		
		PermissionSearchCriteria criteria = new PermissionSearchCriteria();
		criteria.setId(id);
		
		return this.findSingleByCriteria(criteria);
	}

	@Override
	public Permission findSingleByCriteria(PermissionSearchCriteria criteria) throws SQLException {
		Validate.notNull(criteria, "Parametro obbligatorio mancante.");
		if(criteria.getRange() == null) {
			criteria.setMaxRows(1);
		}
		List<Permission> results = this.findByCriteria(criteria);
		if (results != null && !results.isEmpty()) {
			return results.iterator().next();
		}
		
		return null;
	}

	@Override
	public List<Permission> findByCriteria(PermissionSearchCriteria criteria) throws SQLException {
		Validate.notNull(criteria, "Parametro obbligatorio mancante.");
		List<Permission> results = new ArrayList<>();
		
		try (
			PreparedStatementBuilder bld = queryFactory.sqlFindByCriteria(criteria);
			Connection conn = DataSourceUtils.getConnection(dataSource);
			ResultSet rst = bld.executeQuery(conn);
		){
			
			while(rst.next()) {
				Permission permission = new Permission(rst);
				results.add(permission);
			}	
		}
		
		return results;
	}

	@Override
	public Permission create(Permission data) throws SQLException {
		Validate.notNull(data, "Oggetto non valido.");
		
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
	public Permission update(Permission data) throws SQLException {
		Validate.notNull(data, "Oggetto non valido.");
		Validate.notNull(data.getId(), "Parametro obbligatorio mancante: Identificativo privilegio");
		
		try 
		(PreparedStatementBuilder psb = this.queryFactory.sqlUpdate(data)) 
		{
			Connection con = DataSourceUtils.getConnection(dataSource);
			psb.executeUpdate(con);
		}
		
		return data;
	}

}
