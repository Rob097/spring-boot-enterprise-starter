package com.rob.uiapi.dto.models;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rob.core.models.enums.SexEnum;

public class UserR implements UserDetails {
	
private static final long serialVersionUID = -6128934680400004965L;	

	public static class RoleR implements GrantedAuthority{
		
		private static final long serialVersionUID = 7708442137300080212L;		

		public static class PermissionR{		
			public PermissionR() {
				super();
			}			

			
			private Integer id;

			private String name;

			private String description;

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
		
		public RoleR() {
			super();
		}
		
		private Integer id;

		private String name;

		private Set<PermissionR> permissions;

		@Override
		@JsonIgnore
		public String getAuthority() {
			return ""+id;
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

		public Set<PermissionR> getPermissions() {
			return permissions;
		}
		public void setPermissions(Set<PermissionR> permissions) {
			this.permissions = permissions;
		}
		
	}
	
	/* CAMPI DI USERR */

	public UserR() {
		super();
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
	
	private Set<RoleR> roles;

	public UserR(
			String username, 
			String email,
			String password) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
	}
	
	public static RoleR roleById(Integer id) {
		if (id == null) {
			return null;
		}
		
		RoleR role = new RoleR();
		role.setId(id);
		
		return role;
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

	public Set<RoleR> getRoles() {
		return roles;
	}
	public void setRoles(Set<RoleR> roles) {
		this.roles = roles;
	}

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.roles;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	
}
