package com.rob.uiapi.dto.models;

import com.rob.core.utils.java.IntegerList;

public class UserQ {
	
	public static class RoleQ {	
		
		public static class PermissionQ {
			
			public PermissionQ() {
				super();
			}
			
			IntegerList ids;
			
			Integer roleId;
			
			Integer userId;

			public IntegerList getIds() {
				return ids;
			}
			public void setIds(IntegerList ids) {
				this.ids = ids;
			}

			public Integer getRoleId() {
				return roleId;
			}
			public void setRoleId(Integer roleId) {
				this.roleId = roleId;
			}

			public Integer getUserId() {
				return userId;
			}
			public void setUserId(Integer userId) {
				this.userId = userId;
			}
			
		}
		
		public RoleQ() {
			super();
		}

		IntegerList ids;
		
		Integer userId;

		public IntegerList getIds() {
			return ids;
		}
		public void setIds(IntegerList ids) {
			this.ids = ids;
		}

		public Integer getUserId() {
			return userId;
		}
		public void setUserId(Integer userId) {
			this.userId = userId;
		}
		
	}
	
	public UserQ() {
		super();
	}
	
	private String id;
	private String username;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
