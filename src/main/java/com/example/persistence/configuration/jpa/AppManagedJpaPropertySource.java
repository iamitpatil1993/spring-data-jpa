/**
 * 
 */
package com.example.persistence.configuration.jpa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Provider jpa persistence properties to application-managed entity manager factory.
 * 
 * @author amit
 *
 */
@Component
@Qualifier("app-managed")
public class AppManagedJpaPropertySource implements JpaPropertySource {

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

		// Define DataSource properties using environment variables and property
		// sources. (We can't use environment variables in persistence..xml, may be can
		// but not easily.)
		// NOTE: We could externalize the DataSource properties but, it is still using
		// hibernate's native dataSource (connection pool) which is not production reay.
		map.put("javax.persistence.jdbc.url", this.environment.getProperty("db_connection_url"));
		map.put("javax.persistence.jdbc.user", this.environment.getProperty("DB_USER"));
		map.put("javax.persistence.jdbc.password", this.environment.getProperty("DB_PASSWORD"));
		map.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");

		return map;
	}

}
