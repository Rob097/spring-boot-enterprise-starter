package com.rob.core.database;

import org.apache.commons.lang3.StringUtils;

import com.rob.core.models.SYS.User;
import com.rob.core.utils.db.PreparedStatementBuilder;
import com.rob.core.utils.db.QueryFactory;

public class UserManagerQuery extends QueryFactory {

	/**
	 * Metodo per la ricerca dei dati tramite i criteri definiti
	 * @param criteria
	 * @return
	 */
	public PreparedStatementBuilder sqlFindByCriteria(UserSearchCriteria criteria) {
		PreparedStatementBuilder psb = new PreparedStatementBuilder();
		psb.setValidCriteria(criteria.isValidCriteria());
		
		if (criteria.getRange() != null) {
			psb.setRange(criteria.getRange());
		}

		psb.append(" SELECT ");
		
		if(criteria.isCount()) {
			psb.append(" COUNT(*) ");
		}else {		
			psb.append(getSqlFields(User.Field.values(), "USR"));
		}		
		
		psb.append(" FROM " + User.Table + " USR ");
		
		psb.append(" WHERE 1=1 ");
		
		if (StringUtils.isNoneBlank(criteria.getId())) {
			psb.append(" AND USR.USER_ID = ").addBindVariable("USER_ID", criteria.getId());
		}

		if (StringUtils.isNoneBlank(criteria.getUsername())) {
			psb.append(" AND USR.USERNAME = ").addBindVariable("USERNAME", criteria.getUsername());
		}
		
		if (StringUtils.isNoneBlank(criteria.getEmail())) {
			psb.append(" AND USR.EMAIL = ").addBindVariable("EMAIL", criteria.getEmail());
		}

		return psb;
		
	}
	
	/**
	 * Metodo per l'inserimento del dato
	 * @param data
	 * @return
	 */
	public PreparedStatementBuilder sqlCreate(User data) {
		PreparedStatementBuilder psb = new PreparedStatementBuilder();
		
		psb.append(" INSERT INTO ").append(User.Table).append(" (");
		
		psb.append("  USER_ID");
		psb.append(", USERNAME");
		psb.append(", PASSWORD");
		psb.append(", EMAIL");
		psb.append(", NAME");
		psb.append(", SURNAME");
		
		psb.append(" ) VALUES ( ");
		
		psb.addBindVariable("USER_ID", data.getId());
		psb.append(" , ").addBindVariableWithCase("USERNAME", data.getUsername(), false);
		psb.append(" , ").addBindVariableWithCase("PASSWORD", data.getPassword(), false);
		psb.append(" , ").addBindVariableWithCase("EMAIL", data.getEmail(), false);
		psb.append(" , ").addBindVariable("NAME", data.getName());
		psb.append(" , ").addBindVariable("SURNAME", data.getSurname());
		
		psb.append(" ) ");
		
		return psb;
		
	}
	
	/**
	 * Metodo per l'inserimento del dato
	 * @param userId 
	 * @param roleId 
	 * @param data
	 * @return
	 */
	public PreparedStatementBuilder sqlCreateRoleRelation(int userId, int roleId) {
		PreparedStatementBuilder psb = new PreparedStatementBuilder();
		
		psb.append(" INSERT INTO ").append("SYS_USER_ROLES").append(" (");
		
		psb.append("  USER_ID");
		psb.append(", ROLE_ID");
		
		psb.append(" ) VALUES ( ");
		
		psb.addBindVariable("USER_ID", userId);
		psb.append(" , ").addBindVariable("ROLE_ID", roleId);
		
		psb.append(" ) ");
		
		return psb;
		
	}
	
	/**
	 * Metodo per l'aggiornamento del dato
	 * @param data
	 * @return
	 */
	public PreparedStatementBuilder sqlUpdate(User data) {
		PreparedStatementBuilder psb = new PreparedStatementBuilder();

		psb.append(" UPDATE ").append(User.Table);
		psb.append(" SET ");
		
		psb.append(" USER_ID = ").addBindVariable("USER_ID", data.getId());
		
		if (data.getName() != null) {
			psb.append(" ,NAME = ").addBindVariable("NAME", data.getName());
		}
		if (data.getSurname() != null) {
			psb.append(" ,SURNAME = ").addBindVariable("SURNAME", data.getSurname());
		}
		if (data.getAge() != null) {
			psb.append(" ,AGE = ").addBindVariable("AGE", data.getAge());
		}
		if (data.getSex() != null) {
			psb.append(" ,SEX = ").addBindVariable("SEX", data.getSex().getId());
		}
		if (data.getAddress() != null) {
			psb.append(" ,ADDRESS = ").addBindVariable("ADDRESS", data.getAddress());
		}
		
		//The following fields can be updated only by patch Request, not put.
		if (data.getPassword() != null) {
			psb.append(" ,PASSWORD = ").addBindVariableWithCase("PASSWORD", data.getPassword(), false);
		}
		if (data.getEmail() != null) {
			psb.append(" ,EMAIL = ").addBindVariableWithCase("EMAIL", data.getEmail(), false);
		}
		if (data.getUsername() != null) {
			psb.append(" ,USERNAME = ").addBindVariableWithCase("USERNAME", data.getUsername(), false);
		}

		psb.append("  WHERE USER_ID = ").addBindVariable("USER_ID", data.getId());		
		
		return psb;
	}
	
}
