package com.rob.uiapi.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.rob.security.configuration.WebSecurityConfig;


/**
 * @author Roberto97: Class used to allow the restServices to understand the Type of @RequestParam.
 * Without this class the params of view that implements IView doesn't work properly and you'd have Error 500 returned.
 * */
@Configuration
@ComponentScan(excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { WebSecurityConfig.class }) })
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class UIApiWebConfiguration extends WebMvcConfigurationSupport {
	@Autowired
	Jackson2ObjectMapperBuilder jacksonBuilder;

	@Autowired(required = false)
	Formatter<LocalDateTime> localDateTimeFormatter;

	@Autowired(required = false)
	Formatter<LocalDate> localDateFormatter;

	@Autowired(required = false)
	Formatter<LocalTime> localTimeFormatter;

	@Configuration
	public static class Setup {

		@Autowired
		private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

		@PostConstruct
		public void init() throws InstantiationException, IllegalAccessException {
			// I custom resolver, se aggiunti tramite il metodo
			// WebMvcConfigurationSupport.addArgumentResolvers,
			// vengono aggiunti da Spring quasi alla fine della catena dei resolver.
			// Il ViewArgumentResolver si basa sulla annotation @RequestParam e per
			// questo motivo, al fine di istruire spring a valutare prima il
			// ViewArgumentResolver
			// rispetto allo standard
			// org.springframework.web.method.annotation.RequestParamMethodArgumentResolver
			// si è reso necessario agire in fase di post contruction dell'oggetto
			// RequestMappingHandlerAdapter
			List<HandlerMethodArgumentResolver> defaultResolvers = requestMappingHandlerAdapter.getArgumentResolvers();
			List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>(defaultResolvers.size() + 1);
			resolvers.add(viewArgumentResolver());
			resolvers.addAll(defaultResolvers);
			requestMappingHandlerAdapter.setArgumentResolvers(resolvers);

			List<HandlerMethodArgumentResolver> defaultInitResolvers = requestMappingHandlerAdapter
					.getInitBinderArgumentResolvers();
			List<HandlerMethodArgumentResolver> initResolvers = new ArrayList<>(defaultInitResolvers.size() + 1);
			resolvers.add(viewArgumentResolver());
			resolvers.addAll(defaultInitResolvers);
			requestMappingHandlerAdapter.setInitBinderArgumentResolvers(initResolvers);
		}

		@Bean
		public ViewArgumentResolver viewArgumentResolver() {
			return new ViewArgumentResolver();
		}

	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(mappingJackson2HttpMessageConverter());
		addDefaultHttpMessageConverters(converters);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(annotationBindingProcessor());
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(true).defaultContentType(MediaType.APPLICATION_JSON)
				.mediaType("pdf", MediaType.APPLICATION_PDF).mediaType("json", MediaType.APPLICATION_JSON);
	}

	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		return new MappingJackson2HttpMessageConverter(jacksonBuilder.build());
	}

	@Bean
	public AnnotationBindingProcessor annotationBindingProcessor() {
		return new AnnotationBindingProcessor(true);
	}

	@Override
	protected void addFormatters(FormatterRegistry registry) {
		if (localDateTimeFormatter != null) {
			registry.addFormatter(localDateTimeFormatter);
		}
		if (localDateFormatter != null) {
			registry.addFormatter(localDateFormatter);
		}
		if (localTimeFormatter != null) {
			registry.addFormatter(localTimeFormatter);
		}
	}
}
