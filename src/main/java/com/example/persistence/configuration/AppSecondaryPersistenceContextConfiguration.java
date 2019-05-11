/**
 * 
 */
package com.example.persistence.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.persistence.configuration.jpa.JpaPropertySource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Defines configuration for second persistence unit.
 * 
 * @author amit
 *
 */

@Configuration
// specify TM, EntityManagerFactorybean to be used to create repositories.
// specify package where to scan repo definitions.
@PropertySource("classpath:jpa-properties.properties")
@EnableJpaRepositories(basePackages = "com.example.persistence.crm.dao", transactionManagerRef = "jpaPlatformEntityManager", entityManagerFactoryRef = "entityManagerFactoryBean")
public class AppSecondaryPersistenceContextConfiguration {

	@Autowired
	private Environment environment;

	/**
	 * Need to create separate connection pool (DataSource) for another persistence
	 * unit.
	 * This PU connects completely different database.
	 * 
	 * @return DataSource to be used by persistence unit 'my-pu-2'
	 */
	@Bean
	public DataSource hikariCpDataSource2() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(environment.getProperty("db2_connection_url"));
		config.setUsername(environment.getProperty("DB2_USER"));
		config.setPassword(environment.getProperty("DB2_PASSWORD"));

		return new HikariDataSource(config);
	}

	/**
	 * Not using persistence.xml at all, so there is not entry in current
	 * /resources/META-INF/persistence.xml for persistence unit 'my-pu-2' So,
	 * persistece unit is connfigured in pure java way.
	 */
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
			@Qualifier("container-managed") JpaPropertySource jpaPropertySource, JpaVendorAdapter jpaVendorAdapter) {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setPackagesToScan("com.example.persistence.crm.model"); // completely different entities from PU1
		factoryBean.setDataSource(hikariCpDataSource2());
		factoryBean.setPersistenceUnitName(environment.getProperty("PERSISTENCE_UNIT2_NAME"));
		factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
		factoryBean.setJpaPropertyMap(jpaPropertySource.getJpaPropertyMap());
		return factoryBean;
	}

	/**
	 * Since there are two  LocalContainerEntityManagerFactoryBean in application context,
	 * using qualifier to chose LocalContainerEntityManagerFactoryBean for PU2
	 */
	@Bean
	public PlatformTransactionManager jpaPlatformEntityManager(
			@Qualifier("entityManagerFactoryBean") LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactoryBean.getObject());
		return jpaTransactionManager;
	}
}
