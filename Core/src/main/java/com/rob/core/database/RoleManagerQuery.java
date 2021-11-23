package com.rob.core.database;

import com.rob.core.models.SYS.Role;
import com.rob.core.utils.db.PreparedStatementBuilder;
import com.rob.core.utils.db.QueryFactory;

public class RoleManagerQuery extends QueryFactory {

	/**
	 * Metodo per la ricerca dei dati tramite i criteri definiti
	 * @param criteria
	 * @return
	 */
	public PreparedStatementBuilder sqlFindByCriteria(RoleSearchCriteria criteria) {
		PreparedStatementBuilder psb = new PreparedStatementBuilder();
		//psb.setValidCriteria(criteria.isValidCriteria());
		
		if (criteria.getRange() != null) {
			psb.setRange(criteria.getRange());
		}

		psb.append(" SELECT ");
		
		if(criteria.isCount()) {
			psb.append(" COUNT(*) ");
		}else {		
			psb.append(getSqlFields(Role.Field.values(), "ROL"));
		}		
		
		psb.append(" FROM " + Role.Table + " ROL ");
		
		if(criteria.isJoinUser()) {
			psb.append(" INNER JOIN SYS_USER_ROLES USR_ROL ");
			psb.append(" ON ROL.ROLE_ID = USR_ROL.ROLE_ID");
		}
		
		psb.append(" WHERE 1=1 ");
		
		if (criteria.getIds()!= null && !criteria.getIds().isEmpty()) {
			psb.append(" AND ROL.ROLE_ID IN ").addBindVariable("ROLE_ID", criteria.getIds());
		}

		if (criteria.getUserId()!=null) {
			psb.append(" AND USR_ROL.USER_ID = ").addBindVariable("USER_ID", criteria.getUserId());
		}

		return psb;
		
	}
	
	/**
	 * Metodo per l'inserimento del dato
	 * @param data
	 * @return
	 */
	public PreparedStatementBuilder sqlCreate(Role data) {
		PreparedStatementBuilder psb = new PreparedStatementBuilder();
		
		psb.append(" INSERT INTO ").append(Role.Table).append(" (");
		
		psb.append("  ROLE_ID");
		psb.append(", NAME");
		
		psb.append(" ) VALUES ( ");
		
		psb.addBindVariable("ROLE_ID", data.getId());
		psb.append(" , ").addBindVariable("NAME", data.getName());
		
		psb.append(" ) ");
		
		return psb;
		
	}
	
	/**
	 * Metodo per l'inserimento del dato
	 * @param roleId 
	 * @param permission_id 
	 * @return
	 */
	public PreparedStatementBuilder sqlCreatePermissionsRelations(int roleId, int permission_id) {
		PreparedStatementBuilder psb = new PreparedStatementBuilder();
		
		psb.append(" INSERT INTO ").append("SYS_ROLE_PERMISSIONS").append(" (");
		
		psb.append("  ROLE_ID");
		psb.append(", PERMISSION_ID");
		
		psb.append(" ) VALUES ( ");
		
		psb.addBindVariable("ROLE_ID", roleId);
		psb.append(" , ").addBindVariable("PERMISSION_ID", permission_id);
		
		psb.append(" ) ");
		
		return psb;
		
	}
	
	/**
	 * Metodo per l'inserimento del dato
	 * @param roleId
	 * @return
	 */
	public PreparedStatementBuilder sqlDeleteAllPermissionsRelations(int roleId) {
		PreparedStatementBuilder psb = new PreparedStatementBuilder();
		
		psb.append(" DELETE FROM ").append("SYS_ROLE_PERMISSIONS");
		
		psb.append(" WHERE 1=1 ");
		
		psb.append(" AND ROLE_ID = ").addBindVariable("ROLE_ID", roleId);
		
		return psb;
		
	}
	
}
