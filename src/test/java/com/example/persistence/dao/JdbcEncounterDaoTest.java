/**
 * 
 */
package com.example.persistence.dao;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.persistence.BaseTest;
import com.example.persistence.dao.auto.PatientRepository;
import com.example.persistence.model.Encounter;
import com.example.persistence.model.Patient;

/**
 * @author amit
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class JdbcEncounterDaoTest extends BaseTest {

	@Autowired
	private EncounterRepository encounterRepository;

	@Autowired
	private PatientRepository patientRepository;

	/**
	 * Test method for
	 * {@link com.example.persistence.dao.JdbcEncounterDao#add(com.example.persistence.model.Encounter)}.
	 */
	@Test
	public void testAdd() {
		// given
		Patient patient = createTestPatient();
		patientRepository.save(patient);
		
		Calendar now = Calendar.getInstance();
		Encounter encounter = new Encounter();
		encounter.setPatientId(patient.getId());
		encounter.setCreatedTimestamp(now);
		encounter.setUpdatedTimestap(now);
		
		// when
		Encounter createdEncounter = encounterRepository.add(encounter);
		
		assertNotNull(createdEncounter.getEncounterId());
	}

	private Patient createTestPatient() {
		Patient patient = new Patient();
		patient.setFirstName("Bob");
		patient.setLastName("Sargent");
		Calendar dob = Calendar.getInstance();
		dob.add(Calendar.YEAR, -20);
		patient.setDob(dob);
		patient.setSsn(UUID.randomUUID().toString());

		List<String> bloodGroups = Arrays.asList("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+");
		patient.setBloodGroup(bloodGroups.get(ThreadLocalRandom.current().nextInt(0, 8)));
		return patient;
	}

}
