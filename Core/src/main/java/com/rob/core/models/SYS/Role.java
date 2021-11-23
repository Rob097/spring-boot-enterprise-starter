package com.rob.core.models.SYS;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.rob.core.utils.db.QueryFactory;
import com.rob.core.utils.java.ValueObject;
import com.rob.core.utils.java.WithID;


public class Role extends ValueObject implements GrantedAuthority, WithID<Integer>{

	private static final long serialVersionUID = 7503486655601249201L;
	
	public static final String Table = "SYS_ROLES";
	
	/** Campi previsti in tabella role */
	public enum Field {

		ROLE_ID("ROLE_ID"),
		
		NAME("NAME")
		;
		
		private String value;

		Field(String value) {
			this.value = value;
		}

		public String getValue(String prefix) {
			return QueryFactory.getFieldName(this, prefix);
		}

		@Override
		public String toString() {
			return this.value;
		}
		
	}

	private Integer id;
	
	private String name;

	private List<Permission> permissions;
	
	/** Costruttore oggetto */
	public Role() {
		super();
	}

	/**
	 * Costuttore di classe partendo da ResultSet
	 * 
	 * @param rst
	 * @throws SQLException
	 */
	public Role(ResultSet rst) throws SQLException {
		this(rst, "");
	}

	/**
	 * Costruttore oggetto dato resultSet
	 * 
	 * @param rst
	 * @param prefix
	 * @throws SQLException
	 */
	public Role(ResultSet rst, String prefix) throws SQLException {
		super();

		this.setId(rst.getInt(Field.ROLE_ID.getValue(prefix)));
		this.setName(rst.getString(Field.NAME.getValue(prefix)));
		
	}

	public static Role byId(Integer id) {
		if (id == null) {
			return null;
		}
		
		Role role = new Role();
		role.setId(id);
		
		return role;
	}

	@Override
	public String getAuthority() {
		return ""+this.getId();
	}	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	
}
