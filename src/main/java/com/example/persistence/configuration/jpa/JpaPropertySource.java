/**
 * 
 */
package com.example.persistence.configuration.jpa;

import java.util.Map;

/**
 * Provides jpa persistence properties
 * 
 * @author amit
 *
 */
public interface JpaPropertySource {

	public Map<String, Object> getJpaPropertyMap();
}
