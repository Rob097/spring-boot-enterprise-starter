package com.rob.security.configuration;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rob.core.utils.java.ContextListener;

@Configuration
public class WebConfig {
   //Register ContextListener	
   @Bean
   public ServletListenerRegistrationBean<ContextListener> adminInfoListener() {
	   ServletListenerRegistrationBean<ContextListener> listenerRegBean = new ServletListenerRegistrationBean<>();
	   listenerRegBean.setListener(new ContextListener());
	   return listenerRegBean;
   }
} 