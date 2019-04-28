package com.example.persistence.dao.auto;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.persistence.configuration.AppConfiguration;
import com.example.persistence.model.Patient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfiguration.class})
public class PatientRepositoryTest {
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Test
	public void testSave() {
		// given 
		Patient patient = new Patient();
		patient.setFirstName("Bob");
		patient.setLastName("Sargent");
		Calendar dob = Calendar.getInstance();
		dob.add(Calendar.YEAR, -20);
		patient.setDob(dob);
		
		// when
		patientRepository.save(patient);
		
		// then
		assertNotNull(patient.getId());
		assertTrue(patientRepository.existsById(patient.getId()));
	}

	//@Test(expected = NullPointerException.class)
	public void testFindById() {
		fail("Not yet implemented");
	}

	//@Test(expected = NullPointerException.class)
	public void testExistsById() {
		fail("Not yet implemented");
	}

}
