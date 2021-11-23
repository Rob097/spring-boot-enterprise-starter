package com.rob.security.payloads.request;

import javax.validation.constraints.NotBlank;

/**
 * @author Roberto97 This is the entity used for the request of LOGIN. It allows
 *         to pass a username and a password as a single object
 */
public class LoginRequest {
	@NotBlank
	private String username1;

	@NotBlank
	private String password;
	
	private boolean rememberMe;

	public String getUsername() {
		return username1;
	}

	public void setUsername(String username) {
		this.username1 = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}
}
