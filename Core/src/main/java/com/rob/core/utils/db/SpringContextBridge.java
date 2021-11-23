package com.rob.core.utils.db;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextBridge implements ApplicationContextAware{
	
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (SpringContextBridge.applicationContext == null)
			SpringContextBridge.applicationContext = applicationContext;
	}
	
	public static <T> T getBean(Class<T> requiredType) {
		if (applicationContext!=null) {
			return applicationContext.getBean(requiredType);
		} else {
			return null;
		}
	}
	
	public static <T> T getBean(Class<T> requiredType, Object...args) {
		if (applicationContext!=null) {
			return applicationContext.getBean(requiredType, args);
		} else {
			return null;
		}
	}
	
	public static Object getBean(String name) {
		if (applicationContext!=null) {
			return applicationContext.getBean(name);
		} else {
			return null;
		}
	}
	
	public static <T> T getBean(String name, Class<T> requiredType) {
		if (applicationContext!=null) {
			return applicationContext.getBean(name, requiredType);
		} else {
			return null;
		}
	}

	/*public static IModuleService getModuleService(ModuleEnum moduleEnum) {
		return getBean(moduleEnum.getPrefix().toLowerCase()+"ModuleService", IModuleService.class);
	}*/
	
}
