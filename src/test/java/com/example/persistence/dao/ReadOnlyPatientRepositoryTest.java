/**
 * 
 */
package com.example.persistence.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.persistence.BaseTest;
import com.example.persistence.model.Patient;

/**
 * @author amit
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ReadOnlyPatientRepositoryTest extends BaseTest {

	@Autowired
	private ReadOnlyPatientRepository readOnlyPatientRepository;
	
	@Test
	public void testFindById() {
		// when
		Optional<Patient> patient = readOnlyPatientRepository.findById(34723947);
		
		// then
		assertNotNull(patient);
		assertFalse(patient.isPresent());
	}

}
