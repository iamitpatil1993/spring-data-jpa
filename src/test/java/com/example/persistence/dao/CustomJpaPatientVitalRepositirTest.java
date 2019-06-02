package com.example.persistence.dao;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.util.List;
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
import com.example.persistence.dao.projections.PatientVitalProjection;
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
	
	@Test
	public void testFindByIdWithPatientUsingJPQL() {
		// given
		Patient patient = PatientRepositoryTest.createTestPatient();
		patientRepository.save(patient);
		PatientVital patientVital = createTestPatientVial(patient);
		patientVitalRepository.save(patientVital);

		// when
		Optional<PatientVital> patientVitalOptional = patientVitalRepository.findByIdWithPatientUsingJPQL(patientVital.getId());

		// then
		assertThat(patientVitalOptional.isPresent(), is(true));
		assertThat(patientVitalOptional.get().getPatient(), is(notNullValue()));
		assertThat(patientVitalOptional.get().getPatient().getId(), is(equalTo(patient.getId())));
	}
	
	@Test(expected = LazyInitializationException.class)
	public void testFindByIdWithoutPatientUsingJPQL() {
		// given
		Patient patient = PatientRepositoryTest.createTestPatient();
		patientRepository.save(patient);
		PatientVital patientVital = createTestPatientVial(patient);
		patientVitalRepository.save(patientVital);

		// when
		Optional<PatientVital> patientVitalOptional = patientVitalRepository
				.findByIdWithoutPatientUsingJPQL(patientVital.getId());

		// then
		assertThat(patientVitalOptional.isPresent(), is(true));
		assertThat(patientVitalOptional.get().getPatient(), is(nullValue()));
	}
	
	@Test
	public void testGetPatientVitalByIsDeleted() {
		// given
		Patient patient = PatientRepositoryTest.createTestPatient();
		patientRepository.save(patient);
		PatientVital patientVital = createTestPatientVial(patient);
		patientVitalRepository.save(patientVital);
		
		// when
		List<PatientVital> patientVitals = patientVitalRepository.getPatientVitalByIsDeleted(false);

		// then
		patientVitals.stream().forEach(tempPatientVital -> {
			assertThat(tempPatientVital.getPatient(), is(notNullValue()));
			assertThat(tempPatientVital.getPatient().getId(), is(notNullValue()));
			assertThat(tempPatientVital.getPatient().getFirstName(), is(notNullValue()));});
	}
	
	@Test
	public void testGetPatientVitalWithPatientById() {
		// given
		Patient patient = PatientRepositoryTest.createTestPatient();
		patientRepository.save(patient);
		PatientVital patientVital = createTestPatientVial(patient);
		patientVitalRepository.save(patientVital);

		// when
		Optional<PatientVital> patientVitals = patientVitalRepository.getPatientVitalWithPatientById(patientVital.getId());

		// then
		assertThat(patientVitals.isPresent(), is(true));
		assertThat(patientVitals.get().getPatient(), is(notNullValue()));
		assertThat(patientVitals.get().getPatient().getId(), is(notNullValue()));
		assertThat(patientVitals.get().getPatient().getId(), is(equalTo(patient.getId())));
		assertThat(patientVitals.get().getPatient().getFirstName(), is(notNullValue()));
		assertThat(patientVitals.get().getPatient().getFirstName(), is(equalTo(patient.getFirstName())));
	}

	/**
	 * Entity Graph: We can use @EntityGraph annotation to specify entity graph to be used while 
	 * fetching entities using JPQL as well.
	 */
	@Test
	public void testfindPatientVitalByIdUsingEntityGrapghAndCustomQuery() {
		// given
		Patient patient = PatientRepositoryTest.createTestPatient();
		patientRepository.save(patient);
		PatientVital patientVital = createTestPatientVial(patient);
		patientVitalRepository.save(patientVital);

		// when
		Optional<PatientVital> optionalPatientVtial = patientVitalRepository
				.findPatientVitalByIdUsingEntityGrapghAndCustomQuery(patientVital.getId());

		// then
		assertThat(optionalPatientVtial.isPresent(), is(true));
		assertThat(optionalPatientVtial.get().getPatient(), is(notNullValue()));
		assertThat(optionalPatientVtial.get().getPatient().getId(), is(patient.getId()));
	}
	
	/**
	 * Projections : Projection using Interface based projections with Nested associations.
	 */
	@Test
	public void testFindAllByPatient() {
		// given
		Patient patient = PatientRepositoryTest.createTestPatient();
		patientRepository.save(patient);
		PatientVital patientVital = createTestPatientVial(patient);
		patientVitalRepository.save(patientVital);

		// when
		List<PatientVitalProjection> patientVitals = patientVitalRepository.findAllByPatient(patient);

		// then
		assertThat(patientVitals, is(not(empty())));
		assertThat(patientVitals.size(), is(1));
		patientVitals.stream().forEach(patientVitalProj -> {
			// assert patient vital projection
			assertThat(patientVitalProj.getVital(), is(equalTo(patientVital.getVital())));
			assertThat(patientVitalProj.getValue(), is(equalTo(patientVital.getValue())));

			// assert patient projection
			assertThat(patientVitalProj.getPatient(), is(notNullValue()));
			assertThat(patientVitalProj.getPatient().getFirstName(), is(patient.getFirstName()));
			assertThat(patientVitalProj.getPatient().getLastName(), is(patient.getLastName()));
			assertThat(patientVitalProj.getPatient().getId(), is(patient.getId()));
		});
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
