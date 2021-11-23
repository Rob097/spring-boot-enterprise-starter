package com.rob.core.models.SYS;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.rob.core.models.enums.SexEnum;
import com.rob.core.utils.db.QueryFactory;
import com.rob.core.utils.java.ValueObject;
import com.rob.core.utils.java.WithID;

public class User extends ValueObject implements UserDetails, WithID<Integer> {

	private static final long serialVersionUID = 8779524100086734913L;

	public static final String Table = "SYS_USERS";

	/** Campi previsti in tabella user */
	public enum Field {

		// USER_ID VARCHAR(45) No
		USER_ID("USER_ID"),
		
		// NAME VARCHAR(45) No
		NAME("NAME"),

		// SURNAME VARCHAR(45) No
		SURNAME("SURNAME"),
		
		// AGE INT YES
		AGE("AGE"),

		// SEX VARCHAR(45) YES
		SEX("SEX"),

		// USERNAME VARCHAR(45) No
		USERNAME("USERNAME"),
		
		// EMAIL VARCHAR(45) No
		EMAIL("EMAIL"),
		
		// PASSWORD VARCHAR(100) No
		PASSWORD("PASSWORD"),
		
		// ADDRESS VARCHAR(45) YES
		ADDRESS("ADDRESS"),
		
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

	private String surname;
	
	private Integer age;
	
	private SexEnum sex;

	private String username;
	
	private String email;

	private String password;
	
	private String address;

	private List<Role> roles;


	/** Costruttore oggetto */
	public User() {
		super();
	}

	/**
	 * Costuttore di classe partendo da ResultSet
	 * 
	 * @param rst
	 * @throws SQLException
	 */
	public User(ResultSet rst) throws SQLException {
		this(rst, "");
	}

	/**
	 * Costruttore oggetto dato resultSet
	 * 
	 * @param rst
	 * @param prefix
	 * @throws SQLException
	 */
	public User(ResultSet rst, String prefix) throws SQLException {
		super();

		// USER_ID VARCHAR(45) No
		this.setId(rst.getInt(Field.USER_ID.getValue(prefix)));
		// NAME VARCHAR(45) No
		this.setName(rst.getString(Field.NAME.getValue(prefix)));
		// SURNAME VARCHAR(45) No
		this.setSurname(rst.getString(Field.SURNAME.getValue(prefix)));
		// AGE VARCHAR(45) No
		this.setAge(rst.getInt(Field.AGE.getValue(prefix)));
		// SEX VARCHAR(45) No
		this.setSex(SexEnum.byId(rst.getString(Field.SEX.getValue(prefix))));
		// USERNAME VARCHAR(45) No
		this.setUsername(rst.getString(Field.USERNAME.getValue(prefix)));
		// EMAIL VARCHAR(45) No
		this.setEmail(rst.getString(Field.EMAIL.getValue(prefix)));
		// PASSWORD VARCHAR(100) No
		this.setPassword(rst.getString(Field.PASSWORD.getValue(prefix)));
		// ADDRESS VARCHAR(45) No
		this.setAddress(rst.getString(Field.ADDRESS.getValue(prefix)));
		

	}

	public static User byId(Integer id) {
		if (id == null) {
			return null;
		}

		User user = new User();
		user.setId(id);

		return user;
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

	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}

	public SexEnum getSex() {
		return sex;
	}
	public void setSex(SexEnum sex) {
		this.sex = sex;
	}
	
	@Override
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}	

	@Override
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}	

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	public void setRole(Role role) {
		if(role!=null) {
			this.roles = new ArrayList<>();
			this.roles.add(role);
		}
	}

	@Override
	//@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.getRoles();
	}

	@Override
	//@JsonIgnore
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	//@JsonIgnore
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	//@JsonIgnore
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	//@JsonIgnore
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
