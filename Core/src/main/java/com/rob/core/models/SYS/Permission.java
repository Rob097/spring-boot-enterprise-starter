package com.rob.core.models.SYS;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.rob.core.utils.db.QueryFactory;
import com.rob.core.utils.java.ValueObject;
import com.rob.core.utils.java.WithID;

public class Permission extends ValueObject implements WithID<Integer>{

	public static final String Table = "SYS_PERMISSIONS";
	
	/** Campi previsti in tabella permission */
	public enum Field {

		PERMISSION_ID("PERMISSION_ID"),
		
		NAME("NAME"),
		
		DESCRIPTION("DESCRIPTION")
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
	
	private String description;
	

	/** Costruttore oggetto */
	public Permission() {
		super();
	}

	/**
	 * Costuttore di classe partendo da ResultSet
	 * 
	 * @param rst
	 * @throws SQLException
	 */
	public Permission(ResultSet rst) throws SQLException {
		this(rst, "");
	}

	/**
	 * Costruttore oggetto dato resultSet
	 * 
	 * @param rst
	 * @param prefix
	 * @throws SQLException
	 */
	public Permission(ResultSet rst, String prefix) throws SQLException {
		super();

		this.setId(rst.getInt(Field.PERMISSION_ID.getValue(prefix)));
		this.setName(rst.getString(Field.NAME.getValue(prefix)));
		this.setDescription(rst.getString(Field.DESCRIPTION.getValue(prefix)));
		
	}

	public static Permission byId(Integer id) {
		if (id == null) {
			return null;
		}
		
		Permission permission = new Permission();
		permission.setId(id);
		
		return permission;
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
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
