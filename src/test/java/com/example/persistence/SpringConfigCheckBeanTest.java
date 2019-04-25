/**
 * 
 */
package com.example.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.persistence.configuration.AppConfiguration;

/**
 * @author amit
 *
 */
@ContextConfiguration(classes = AppConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringConfigCheckBeanTest {

	@Autowired
	private SpringConfigCheckBean springConfigCheckBean;
	
	/**
	 * Test method for {@link com.example.persistence.SpringConfigCheck#sayHello()}.
	 */
	@Test
	public void testSayHello() {
		// when
		springConfigCheckBean.sayHello();
	}

}
