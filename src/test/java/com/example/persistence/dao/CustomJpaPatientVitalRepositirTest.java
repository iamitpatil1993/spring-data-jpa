package com.example.persistence.dao;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.hibernate.LazyInitializationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.persistence.BaseTest;
import com.example.persistence.dao.auto.PatientRepository;
import com.example.persistence.dao.auto.PatientRepositoryTest;
import com.example.persistence.model.Patient;
import com.example.persistence.model.PatientVital;
import com.example.persistence.model.VitalType;

@RunWith(SpringJUnit4ClassRunner.class)
public class CustomJpaPatientVitalRepositirTest extends BaseTest {

	@Autowired
	private PatientVitalRepository patientVitalRepository;

	@Autowired
	private PatientRepository patientRepository;

	/**
	 * Object graph will load patient entity (in single value association) despite it is marked to be loaded lazily
	 */
	@Test
	public void testFindByIdWithPatient() {
		// given
		Patient patient = PatientRepositoryTest.createTestPatient();
		patientRepository.save(patient);
		PatientVital patientVital = createTestPatientVial(patient);
		patientVitalRepository.save(patientVital);

		// when
		Optional<PatientVital> patientVitalOptional = patientVitalRepository.findByIdWithPatient(patientVital.getId());

		// then
		assertThat(patientVitalOptional.isPresent(), is(true));
		assertThat(patientVitalOptional.get().getPatient(), is(notNullValue()));
		assertThat(patientVitalOptional.get().getPatient().getId(), is(equalTo(patient.getId())));

	}

	/**
	 * If we do not specify object graph, default object graph is used, which is
	 * static fetch types in entity. Since, patient association is marked to be
	 * lazy, patinet will not be fetched in default entity fetch graph used by
	 * spring generated repo. impl.
	 */
	@Test(expected = LazyInitializationException.class)
	public void testFindByIdWithoutPatient() {
		// given
		Patient patient = PatientRepositoryTest.createTestPatient();
		patientRepository.save(patient);
		PatientVital patientVital = createTestPatientVial(patient);
		patientVitalRepository.save(patientVital);

		// when
		Optional<PatientVital> patientVitalOptional = patientVitalRepository.findById(patientVital.getId());

		// then
		assertThat(patientVitalOptional.isPresent(), is(true));
		assertThat(patientVitalOptional.get().getPatient(), is(nullValue()));
	}

	private PatientVital createTestPatientVial(Patient patient) {
		PatientVital patientVital = new PatientVital();
		patientVital.setPatient(patient);
		patientVital.setValue(2323d);
		patientVital.setVital(VitalType.HEIGHT);
		return patientVital;
	}


	@After
	@Before
	public void beforeAndAfterTest() {
		patientVitalRepository.deleteAllInBatch();
	}


}
