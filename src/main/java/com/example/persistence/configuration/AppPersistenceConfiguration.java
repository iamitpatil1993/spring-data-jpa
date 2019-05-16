 package com.example.persistence.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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
// @EnableJpaRepositories expects transaction manager with name 'transactionManager' and EM Factory as 'entitymanagerFactory' as default.
// if we have beans with different names, we can override these default using transactionManagerRef and entitymanagerFactoryRef
@EnableJpaRepositories(basePackages = "com.example.persistence.dao") // we must set basePackages to scan, we can change default postfix ("Impl") using repositoryImplementationPostfix
public class AppPersistenceConfiguration {

	@Autowired
	public Environment environment;

	/**
	 * @EnableJpaRepository expects transaction manager bean with name 'transactionManager' as a default. So,
	 * adding additional bean name to match defaults. We can set/override default name using transactionManagerRef attribute 
	 * of @EnableJpaRepositories annotation, but I don't want o hard code bean name, and want to chose one based on active profile.
	 * 
	 * @param entityManagerFactoryBean
	 * @return
	 */
	@Bean(name = "transactionManager")
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
	@Bean(name = "entityManagerFactory") // additional bean name to match default name expected by @EnableJpaRepository
	@Profile("dev")
	public LocalEntityManagerFactoryBean localEntityManagerFactoryBean(JpaPropertySource jpaPropertySource) {
		LocalEntityManagerFactoryBean entityManagerFactoryBean = new LocalEntityManagerFactoryBean();
		entityManagerFactoryBean.setPersistenceUnitName(environment.getProperty("PERSISTENCE_UNIT_NAME"));

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

	@Bean(name = "entityManagerFactory") // additional bean name to match default name expected by @EnableJpaRepository
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
		
		// Enables asynchronous bootstrapping of JPA in separate thread. So, JPA bootstrapping and other application.
		// So, JPA and other bootrapping will be in parallel in separate threads.
		// refer https://docs.spring.io/spring/docs/current/spring-framework-reference/data-access.html#orm-jpa-setup-background
		containerEntityManagerFactoryBean.setBootstrapExecutor(new SimpleAsyncTaskExecutor());  
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
