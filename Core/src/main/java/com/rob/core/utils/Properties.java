package com.rob.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;


public class Properties extends java.util.Properties{
	
	private static final long serialVersionUID = -4259272132135580295L;

	private ClassLoader loader = Thread.currentThread().getContextClassLoader();
	
	public Properties() {
		super();
	}
	
	public Properties(String name) {
		super();
		
		try (InputStream resourceStream = loader.getResourceAsStream(name)) {
		    this.load(resourceStream);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	
	/* CONSTANTS */
	public static List<String> ALLOWED_ORIGINS = Arrays.asList("http://localhost:4200", "https://myportfolio-6a671.web.app");
	public static List<String> ALLOW_METHODS = Arrays.asList("OPTIONS", "GET", "POST", "PUT", "DELETE");
	public static List<String> ALLOW_HEADERS = Arrays.asList("*");
	
	public static String Id_AUTHORITIES = "authority";
	
	public static String CLAIM_KEY_AUTHORITIES = "roles";
	public static String USER_ID_ATTRIBUTE = "userId";
	
	//ROLES
	public static String ROLE_BASIC = "BASIC";
	public static String ROLE_ADMIN = "ADMIN";
	public static String ROLE_EDITOR = "EDITOR";

}
