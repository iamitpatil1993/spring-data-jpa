package com.example.persistence.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Top/Root level app configuration class
 * 
 * @author amit
 *
 */

@Configuration
@ComponentScan(basePackages = "com.example.persistence")
@Import(value = { AppPersistenceConfiguration.class, AppSecondaryPersistenceContextConfiguration.class })
//@Import(value = { AppPersistenceConfiguration.class })
public class AppConfiguration {
	// Nothing to do here for now.

}
