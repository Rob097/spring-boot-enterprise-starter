package com.rob.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Class of the entire project.
 * 
 * There are some annotations that are really important for the correct working of the application.
 * 
 * @EnableAutoConfiguration: Enable auto-configuration of the Spring Application Context, attempting to guess and
 * configure beans that you are likely to need. For more information look inside the annotation. The exclude parameters are foundamental because in this project
 * has been congigured a manually manage of the Hicari connection and in order to do so is necessary to deactivate the auto configuration of connection provided by this annotation.
 * 
 * @ComponentScan, @EntityScan and @EnableJpaRepositories are used to make things work afeter the division of the project in multiple sub modules (Core, UIApi, ecc...).
 * 
 * All these annotation could be replaced by @SpringBootApplication. But from the moment that we need some customizations from each one is easier to use the single ones.
 * 
 * @author Roberto97
 * 
 * */

@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ComponentScan(basePackages = { "com.rob.*" })
@EntityScan(basePackages = { "com.rob.*" })
@EnableJpaRepositories(basePackages = { "com.rob.*" })
public class PortfolioApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortfolioApplication.class, args);
	}

}
