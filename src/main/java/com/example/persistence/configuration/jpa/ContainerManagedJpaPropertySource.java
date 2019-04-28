/**
 * 
 */
package com.example.persistence.configuration.jpa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Provider jpa persistence properties to container-managed entity manager factory.
 * 
 * @author amit
 *
 */
@Component
@Profile(value = { "int", "prod" })
public class ContainerManagedJpaPropertySource implements JpaPropertySource {

	@Autowired
	private Environment environment;

	/**
	 * Read JpaProperties from external and build property map
	 * 
	 * @return JpaProperty Map
	 */
	@Override
	public Map<String, Object> getJpaPropertyMap() {
		Map<String, Object> map = new HashMap<>(3);

		map.put("hibernate.show_sql", this.environment.getProperty("hibernate.show_sql"));
		map.put("hibernate.format_sql", this.environment.getProperty("hibernate.format_sql"));
		map.put("use_sql_comments", this.environment.getProperty("use_sql_comments"));
		map.put("hibernate.hbm2ddl.auto", this.environment.getProperty("hibernate.hbm2ddl.auto"));
		
		return map;
	}
}
