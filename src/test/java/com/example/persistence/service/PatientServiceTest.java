/**
 * 
 */
package com.example.persistence.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.persistence.BaseTest;

/**
 * @author amit
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PatientServiceTest extends BaseTest {

	@Autowired
	private PatientService patientService;
	
	/**
	 * Test method for {@link com.example.persistence.service.PatientService#stressTestUsingPagination()}.
	 */
	//@Test
	public void testStressTestUsingPagination() {
		patientService.stressTestUsingPagination();
	}
	
	/**
	 * Test method for {@link com.example.persistence.service.PatientService#stressTestUsingStream()}.
	 */
	@Test
	public void testStressTestUsingStream() {
		patientService.stressTestUsingStream();
	}

}
