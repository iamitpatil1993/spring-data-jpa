 package com.example.persistence.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.persistence.configuration.jpa.JpaPropertySource;
import com.example.persistence.dao.JPA;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Configuration class defines configurations for DataSource and ORM.
 * 
 * @author amit
 *
 */

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:jpa-properties.properties")
public class AppPersistenceConfiguration {

	@Autowired
	Environment environment;

	@Value("${PERSISTENCE_UNIT_NAME}")
	private String persistenceUnitName;

	@Bean
	public PlatformTransactionManager jpaTransactionManager(AbstractEntityManagerFactoryBean entityManagerFactoryBean) {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactoryBean.getObject());
		return jpaTransactionManager;

	}

	/**
	 * In case of this LocalEntityManagerFactoryBean, we can not use configured
	 * dataSourec in spring application context. Also, datasource defined by
	 * hibernate is not production read and not be used in production environments.
	 * 
	 * So, this LocalEntityManagerFactoryBean should be used in standalone
	 * applications or in development environments only. And for production ready
	 * application we should consider using LocalContainerEntityManagerFactoryBean,
	 * which we will see in upcoming tutorials.
	 * 
	 * @return LocalEntityManagerFactoryBean
	 */
	@Bean
	@Profile("dev")
	public LocalEntityManagerFactoryBean localEntityManagerFactoryBean(JpaPropertySource jpaPropertySource) {
		LocalEntityManagerFactoryBean entityManagerFactoryBean = new LocalEntityManagerFactoryBean();
		entityManagerFactoryBean.setPersistenceUnitName(persistenceUnitName);

		// We can set/override properties set in persistence.xml
		entityManagerFactoryBean.setJpaPropertyMap(jpaPropertySource.getJpaPropertyMap());

		return entityManagerFactoryBean;
	}

	@Bean
	@Profile(value = { "int", "prod" })
	public DataSource hikariCpDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(this.environment.getProperty("db_connection_url"));
		config.setUsername(this.environment.getProperty("DB_USER"));
		config.setPassword(this.environment.getProperty("DB_PASSWORD"));

		return new HikariDataSource(config);
	}

	@Bean
	@Profile(value = { "int", "prod" })
	public JpaVendorAdapter hibernateJpaVendorAdapter() {
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setDatabase(Database.POSTGRESQL);
		jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
		jpaVendorAdapter.setGenerateDdl(true);
		jpaVendorAdapter.setShowSql(true);

		return jpaVendorAdapter;
	}

	@Bean
	@Profile(value = { "int", "prod" })
	public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(DataSource dataSource,
			JpaVendorAdapter adapter, JpaPropertySource jpaPropertySource) {
		LocalContainerEntityManagerFactoryBean containerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		containerEntityManagerFactoryBean.setDataSource(dataSource);
		containerEntityManagerFactoryBean.setJpaVendorAdapter(adapter);

		// We must to define this if persistenec.xml file it not used at all. Spring
		// needs to know location of entities.
		// So, either persistence.xml or this is required.
		// refer:
		// https://stackoverflow.com/questions/37496293/spring-jpa-java-lang-illegalstateexception-no-persistence-units-parsed-from-cl
		containerEntityManagerFactoryBean.setPackagesToScan("com.example.persistence.model");

		// we can define persistence-unit here, which does not event belongs to any
		// persistence.xml file.
		// so, it's like creating pu on the fly using java config.
		// It's completely ok to omit setting this, in that case persistence unit name
		// will be 'default'.
		containerEntityManagerFactoryBean.setPersistenceUnitName("foo-unit");
		
		// set jpa properties programmatically
		containerEntityManagerFactoryBean.setJpaPropertyMap(jpaPropertySource.getJpaPropertyMap());
		return containerEntityManagerFactoryBean;
	}
	
	/**
	 * This is post processor bean that does/enables the translation of persistene
	 * exceptions into to generic spring specfic exception hierarch topped by
	 * DataAccessException.
	 * 
	 * This bean actually enables exception translation for beans in application
	 * context marked with @Repository annotation. It actually enables one advice
	 * PersistenceExceptionTranslationAdvisor on pointcut that matches all beans in
	 * application context that are annoted with @Repository annotation.
	 * 
	 * After just defining this bean, all beans in application context annoted
	 * with @Repository will get advised to translate exceptions.
	 * 
	 * @return
	 */
	@Bean
	public BeanPostProcessor persistenceExceptonTranslationPostProcessor() {

		PersistenceExceptionTranslationPostProcessor postProcessor = new PersistenceExceptionTranslationPostProcessor();

		// we can change the pointcut to match something different annotation, may be
		// custom.
		// Since out repository class CustomJpaEmployeeDao is annoted with custom
		// annotation JPA, it will get advised and exceptions will get translated

		// postProcessor.setRepositoryAnnotationType(JPA.class);; 	// uncomment this to
																	// match classes annoted with @Jpa to get advise for translation instead of
																	// @Repository. (which is default)

		return postProcessor;
	}
}
