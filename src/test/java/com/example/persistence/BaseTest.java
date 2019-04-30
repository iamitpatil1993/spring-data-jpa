package com.example.persistence;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

import com.example.persistence.configuration.AppConfiguration;

/**
 * 
 */

/**
 * @author amit
 *
 */
@ContextConfiguration(classes = {AppConfiguration.class})
public class BaseTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

	// added feature in Junit4.7, which we can use to get name of currently executing test.
	@Rule public TestName name = new TestName();
	
	@Before
	public void beforeTest() {
		/// this will help debugging logs and viewing queries executed per test case.
		String testName = name.getMethodName(); 
		LOGGER.info("\n\n---------------------- Executing - {} ---------------------", testName);	
	}
}
