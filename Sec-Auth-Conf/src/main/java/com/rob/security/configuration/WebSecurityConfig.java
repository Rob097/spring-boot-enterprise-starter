package com.rob.security.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.rob.core.utils.Properties;
import com.rob.security.jwt.AuthEntryPointJwt;
import com.rob.security.jwt.AuthTokenFilter;
import com.rob.security.services.AuthenticationUserService;

/**
 * @author Roberto97
 * This is the most important class to configure and manage the Spring Security and the JWT
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	/**
	 * USER_MATCHER: Path where you need to have the role of user to access at.
	 * ADMIN_MATCHER: Path where you need to have the role of admin to access at.
	 * MOD_MATCHER: Path where you need to have the moderator of user to access at.
	 */
	private static final String[] BASIC_MATCHER = { "/api/clienti/cerca/**", "/api/companies/**"};
	private static final String[] ADMIN_MATCHER = { "/api/clienti/inserisci/**", "/api/clienti/elimina/**", "/api/auth/getAll" };
	private static final String[] EDITOR_MATCHER = {};

	
	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}
	
	@Bean
	public AuthenticationUserService getUserService() {
		return new AuthenticationUserService();
	}



	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(getUserService()).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/**
	 * To crypt the password bCrypt is used.
	 * @return bCrypt password encoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	
	/**
	 * Principal method of configuration for this class.
	 * .cors() is needed to allow the application to manage the CORS POLICY errors.
	 * The method is easy to understand if you read it.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
			.cors().configurationSource(corsConfigurationSource()).and()
			.csrf().disable()
			.formLogin().disable()
			.httpBasic().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()			
			.authorizeRequests()		
			//.anyRequest().permitAll();
			.antMatchers("/*.css", "/*.png", "/*.js", "/*.json", "/*.ico").permitAll()
			.antMatchers("/static/**").permitAll()//Molto importante per il primo caricamento
			.antMatchers(HttpMethod.OPTIONS).permitAll()
			.antMatchers("/api/auth/**").permitAll()
			.antMatchers("/api/test/**").permitAll()
			.antMatchers(BASIC_MATCHER).hasAnyRole(Properties.ROLE_BASIC)
			.antMatchers(ADMIN_MATCHER).hasAnyRole(Properties.ROLE_ADMIN)
			.antMatchers(EDITOR_MATCHER).hasAnyRole(Properties.ROLE_EDITOR)
			.anyRequest().authenticated();
			
		
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		
		// disable page caching
		http.headers().cacheControl();
	}
	
	//This can be customized as required
	CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    List<String> allowOrigins = Properties.ALLOWED_ORIGINS;
	    List<String> allowMethods = Properties.ALLOW_METHODS;
	    List<String> allowHeaders = Properties.ALLOW_HEADERS;
	    configuration.setAllowedOrigins(allowOrigins);
	    configuration.setAllowedMethods(allowMethods);
	    configuration.setAllowedHeaders(allowHeaders);
	    //in case authentication is enabled this flag MUST be set, otherwise CORS requests will fail
	    configuration.setAllowCredentials(true);
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}

}
