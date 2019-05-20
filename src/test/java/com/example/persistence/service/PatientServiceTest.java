/**
 * 
 */
package com.example.persistence.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.persistence.BaseTest;
import com.example.persistence.dao.auto.PatientRepositoryTest;
import com.example.persistence.model.Patient;

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
	//@Test
	public void testStressTestUsingStream() {
		patientService.stressTestUsingStream();
	}
	
	/**
	 * Test method for {@link com.example.persistence.service.PatientService#saveInBatch()}.
	 */
	@Test
	public void testSaveInBatch() {
		int count = 10000;
		List<Patient> patients = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			patients.add(PatientRepositoryTest.createTestPatient());
		}
		patientService.saveInBatch(patients);
	}

}
