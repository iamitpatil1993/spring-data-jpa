package com.example.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
/**
 * Health check bean to test spring configuration, maven dependencies are all correct.
 * 
 * @author amit
 *
 */
@Component
public class SpringConfigCheckBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfigCheckBean.class);
	
	public void sayHello() {
		LOGGER.info("Spring-data-Jpa test");
	}
}
