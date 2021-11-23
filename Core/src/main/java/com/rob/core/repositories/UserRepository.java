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

import com.rob.core.database.UserManagerQuery;
import com.rob.core.database.UserSearchCriteria;
import com.rob.core.fetch.UserFetchHandler;
import com.rob.core.fetch.modules.Fetch;
import com.rob.core.fetch.modules.FetchBuilder;
import com.rob.core.models.SYS.User;
import com.rob.core.utils.db.PreparedStatementBuilder;

@Repository
public class UserRepository implements IUserRepository {

	@Autowired
	private DataSource dataSource;

	private final UserManagerQuery queryFactory;
	
	@Autowired
	private UserFetchHandler fetchHandler;

	public UserRepository() {
		queryFactory = new UserManagerQuery();
	}

	@Override
	public User findById(String id) throws SQLException {
		Validate.notEmpty(id, "Parametro obbligatorio mancante: identificativo utente.");

		return this.findById(id, FetchBuilder.none());
	}
	
	@Override
	public User findById(String id, Fetch fetch) throws SQLException {
		Validate.notEmpty(id, "Parametro obbligatorio mancante: identificativo utente.");
		
		UserSearchCriteria criteria = new UserSearchCriteria();
		criteria.setId(id);
		criteria.setFetch(fetch);
		
		return this.findSingleByCriteria(criteria);
	}

	@Override
	public User findSingleByCriteria(UserSearchCriteria criteria) throws SQLException {
		Validate.notNull(criteria, "Parametro obbligatorio mancante.");
		if(criteria.getRange() == null) {
			criteria.setMaxRows(1);
		}
		List<User> results = this.findByCriteria(criteria);
		if (results != null && !results.isEmpty()) {
			return results.iterator().next();
		}
		
		return null;
	}
	
	@Override
	public List<User> findByCriteria(UserSearchCriteria criteria) throws SQLException {
		Validate.notNull(criteria, "Parametro obbligatorio mancante.");
		List<User> results = new ArrayList<>();
		Fetch fetch = criteria.getFetch();
		
		try (
			PreparedStatementBuilder bld = queryFactory.sqlFindByCriteria(criteria);
			Connection conn = DataSourceUtils.getConnection(dataSource);
			ResultSet rst = bld.executeQuery(conn);
		){			
			while(rst.next()) {
				User user = new User(rst);
				fetchHandler.handle(user, fetch, rst);
				results.add(user);
			}	
		}
		
		return results;
	}

	@Override
	public User create(User data) throws SQLException {
		Validate.notNull(data, "Oggetto non valido.");
		
		try 
		(PreparedStatementBuilder psb = this.queryFactory.sqlCreate(data)) 
		{
			Connection con = DataSourceUtils.getConnection(dataSource);
			psb.executeUpdate(con);
		}
		
		return data;
	}
	
	@Override
	public void createRoleRelation(int userId, int roleId) throws SQLException {
		Validate.notNull(userId, "Oggetto non valido: userId");
		Validate.notNull(roleId, "Oggetto non valido: roleId");
		
		try 
		(PreparedStatementBuilder psb = this.queryFactory.sqlCreateRoleRelation(userId, roleId)) 
		{
			Connection con = DataSourceUtils.getConnection(dataSource);
			psb.executeUpdate(con);
		}
		
	}
	
	@Override
	public User update(User data) throws SQLException {
		Validate.notNull(data, "Oggetto non valido.");
		Validate.notNull(data.getId(), "Oggetto non valido. Id mancante");
		
		try 
		(PreparedStatementBuilder psb = this.queryFactory.sqlUpdate(data)) 
		{
			Connection con = DataSourceUtils.getConnection(dataSource);
			psb.executeUpdate(con);
		}
		
		return data;
	}

}
