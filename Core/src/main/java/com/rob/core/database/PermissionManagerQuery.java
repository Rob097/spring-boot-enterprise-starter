package com.rob.core.database;

import com.rob.core.models.SYS.Permission;
import com.rob.core.utils.db.PreparedStatementBuilder;
import com.rob.core.utils.db.QueryFactory;

public class PermissionManagerQuery extends QueryFactory {

	/**
	 * Metodo per la ricerca dei dati tramite i criteri definiti
	 * @param criteria
	 * @return
	 */
	public PreparedStatementBuilder sqlFindByCriteria(PermissionSearchCriteria criteria) {
		PreparedStatementBuilder psb = new PreparedStatementBuilder();
		psb.setValidCriteria(criteria.isValidCriteria());
		
		if (criteria.getRange() != null) {
			psb.setRange(criteria.getRange());
		}

		psb.append(" SELECT ");
		
		if(criteria.isCount()) {
			psb.append(" COUNT(*) ");
		}else {		
			psb.append(getSqlFields(Permission.Field.values(), "PRM"));
		}		
		
		psb.append(" FROM " + Permission.Table + " PRM ");
		
		if(criteria.isJoinRoles()) {
			psb.append(" INNER JOIN SYS_ROLE_PERMISSIONS ROL_PRM ");
			psb.append(" ON PRM.PERMISSION_ID = ROL_PRM.PERMISSION_ID");
		}
		
		psb.append(" WHERE 1=1 ");
		
		if (criteria.getIds()!= null && !criteria.getIds().isEmpty()) {
			psb.append(" AND PRM.PERMISSION_ID IN ").addBindVariable("PERMISSION_ID", criteria.getIds());
		}

		if (criteria.getRoleId()!=null) {
			psb.append(" AND ROL_PRM.ROLE_ID = ").addBindVariable("ROLE_ID", criteria.getRoleId());
		}

		return psb;
		
	}
	
	/**
	 * Metodo per l'inserimento del dato
	 * @param data
	 * @return
	 */
	public PreparedStatementBuilder sqlCreate(Permission data) {
		PreparedStatementBuilder psb = new PreparedStatementBuilder();
		
		psb.append(" INSERT INTO ").append(Permission.Table).append(" (");
		
		psb.append(" NAME");
		psb.append(", DESCRIPTION");
		
		psb.append(" ) VALUES ( ");
		
		psb.addBindVariable("NAME", data.getName());
		psb.append(" , ").addBindVariable("DESCRIPTION", data.getDescription());
		
		psb.append(" ) ");
		
		return psb;
		
	}
	
	/**
	 * Metodo per l'aggiornamento del dato
	 * @param data
	 * @return
	 */
	public PreparedStatementBuilder sqlUpdate(Permission data) {
		PreparedStatementBuilder psb = new PreparedStatementBuilder();

		psb.append(" UPDATE ").append(Permission.Table);
		psb.append(" SET ");
		
		if (data.getName() != null) {
			psb.append(" NAME = ").addBindVariable("NAME", data.getName());
		}
		if (data.getDescription() != null) {
			psb.append(" ,DESCRIPTION = ").addBindVariable("DESCRIPTION", data.getDescription());
		}

		psb.append("  WHERE PERMISSION_ID = ").addBindVariable("PERMISSION_ID", data.getId());		
		
		return psb;
	}
	
}
