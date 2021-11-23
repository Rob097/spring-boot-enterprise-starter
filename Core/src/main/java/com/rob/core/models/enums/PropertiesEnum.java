package com.rob.core.models.enums;

public enum PropertiesEnum {
	
	/*NOMI FILE DI PROPERTIES*/
	MAIN_PROPERTIES("application-main.properties"),
	MAC_PROPERTIES("application-mac.properties"),
	
	/*CONENSSIONE DATABASE*/
	HOST("spring.datasource.host"),
	URL("spring.datasource.url"),
	SSL("spring.datasource.ssl"),
	USERNAME("spring.datasource.username"),
	PASSWORD("spring.datasource.password"),
	
	
	/*SECURITY AND AUTHENTICATION*/
	TOKEN_HEADER("jwtHeader"),
	TOKEN_CONSTANT("jwtConstant"),
	JWT_EXPIRATION("jwtExpirationMs"),
	JWT_EXPIRATION_REMEMBER_ME("jwtExpirationMsRememberMe"),
	JWT_SECRET("jwtSecret"),
	JWT_CURRENT_DOMAIN("jwtCurrentDomain"),
	ENCRYPT_PASSWORD("secret.password"),
	ENCRYPT_SALT("secret.salt"),
	ENCRYPT_ENABLED("secret.enabled"),
	ENCRYPT_ALGORITHM("secret.algorithm"),
	;
	
	private String name;
	
	PropertiesEnum(String name) {
		this.name = name;
	};
	
	public String getName() {
		return name;
	}
	
}
