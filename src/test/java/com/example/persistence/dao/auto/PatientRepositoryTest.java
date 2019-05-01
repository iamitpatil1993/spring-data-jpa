package com.example.persistence.dao.auto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.persistence.BaseTest;
import com.example.persistence.model.Patient;

@RunWith(SpringJUnit4ClassRunner.class)
public class PatientRepositoryTest extends BaseTest {

	@Autowired
	private PatientRepository patientRepository;
	
	/**
	 * QueryByExampleExecutor: count  
	 */
	@Test
	public void testCountByUsingQBE() {
		// when
		// create probe to be passed to Example
		Patient patient = new Patient();
		patient.setDeleted(false);
		patient.setFirstName(UUID.randomUUID().toString());

		// when
		Example<Patient> example = Example.of(patient); // build example to match probe exactly with non-null examples 
		long count = patientRepository.count(example); // fetch count Example

		// then
		assertEquals(0, count);
	}

	/**
	 * QueryByExampleExecutor: Creates ExampleMatcher to match records by firstName or lastName
	 */
	@Test
	public void testFindAllUsingQBEByFirstNameOrLastName() {
		// when
		// create probe to be passed to Example
		Patient patient = new Patient();
		patient.setFirstName(UUID.randomUUID().toString());
		patient.setLastName(UUID.randomUUID().toString());

		// when
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny(); // create ExampleMatcher to add conditions on matching. (Adding OR condition instead of AND)

		Example<Patient> example = Example.of(patient, exampleMatcher); // build example to match probe exactly with non-null examples values.
		List<Patient> patients = patientRepository.findAll(example); 

		// then
		assertTrue(patients.size() >= 0);
	}


	/**
	 * QueryByExampleExecutor: Creates ExampleMatcher to match records by firstName And lastName
	 */
	@Test
	public void testFindAllUsingQBEByFirstNameAndLastName() {
		// when
		// create probe to be passed to Example
		Patient patient = new Patient();
		patient.setFirstName(UUID.randomUUID().toString());
		patient.setLastName(UUID.randomUUID().toString());

		// when
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll(); // matchingAll() expects all properties to match so creates AND condition.

		Example<Patient> example = Example.of(patient, exampleMatcher); // build example to match probe exactly with non-null examples values.
		List<Patient> patients = patientRepository.findAll(example); 

		// then
		assertEquals(0, patients.size());
	}

	/**
	 * QueryByExampleExecutor: Creates ExampleMatcher to match records by firstName And lastName with contains condition and ignorecase
	 */
	@Test
	public void testFindAllUsingQBEByFirstNameAndLastNameIgnoreCase() {
		// when
		// create probe to be passed to Example
		Patient patient = new Patient();
		patient.setFirstName(UUID.randomUUID().toString());
		patient.setLastName(UUID.randomUUID().toString());

		// when
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll() // matchingAll() conditions applicable for all.
				.withIgnoreCase() // withIgnoreCase() ignore case for all string fields
				.withStringMatcher(StringMatcher.CONTAINING); // contains condition for all string fields.
		Example<Patient> example = Example.of(patient, exampleMatcher); 
		List<Patient> patients = patientRepository.findAll(example);

		// then
		assertEquals(0, patients.size());
	}

	/**
	 * QueryByExampleExecutor: Creates ExampleMatcher to create field specific matching condition.
	 */
	@Test
	public void testFindAllUsingQBEByFirstNameStartsWithAndLastNameEndsWithAndIgnoreCaseForLastName() {
		// when
		// create probe to be passed to Example
		Patient patient = new Patient();
		patient.setFirstName(UUID.randomUUID().toString());
		patient.setLastName(UUID.randomUUID().toString());

		// when
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll() // matchingAll() conditions applicable for all.
				.withMatcher("firstName", GenericPropertyMatchers.startsWith()) // firstName starts with
				.withMatcher("lastName", GenericPropertyMatchers.endsWith()) // lastName endswith
				.withIgnoreCase("lastName");  // Ignore case for lastName only
		Example<Patient> example = Example.of(patient, exampleMatcher); 
		List<Patient> patients = patientRepository.findAll(example);

		// then
		assertEquals(0, patients.size());
	}

	/**
	 * QueryByExampleExecutor: Creates ExampleMatcher to ignore matching of specific fields.
	 */
	@Test
	public void testFindAllUsingQBEByFirstNameAndLastNameIgnoringIsDeleted() {
		// when
		// create probe to be passed to Example
		Patient patient = new Patient();
		patient.setFirstName(UUID.randomUUID().toString());
		patient.setLastName(UUID.randomUUID().toString());
		patient.setDeleted(false);

		// when
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll() // matchingAll() conditions applicable for all.
				.withIgnorePaths("isDeleted"); // removes isDeleted from condition, irrespective of probe has value or null
		Example<Patient> example = Example.of(patient, exampleMatcher);
		List<Patient> patients = patientRepository.findAll(example);

		// then
		assertEquals(0, patients.size());
	}

	/**
	 * QueryByExampleExecutor: Creates ExampleMatcher with PropertyValueTransformer, to transform property value before use in query.
	 */
	@Test
	public void testFindAllUsingQBEByFirstNameWithPropertyValueTransformer() {
		// when
		// create probe to be passed to Example
		Patient patient = new Patient();
		StringBuilder firsNameBuilder = new StringBuilder("\t");
		firsNameBuilder.append(UUID.randomUUID().toString()).append("\t");
		patient.setFirstName(firsNameBuilder.toString());

		// when
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll() // matchingAll() conditions applicable for all.
				.withTransformer("firstName", (Optional<Object> firstName) -> {
					if (firstName.isPresent()) {
						return Optional.of(firstName.get().toString().trim()); // trims (transforms) firstName in Probe
						// before passing to query.
					}
					return null;
				});
		Example<Patient> example = Example.of(patient, exampleMatcher);
		List<Patient> patients = patientRepository.findAll(example);

		// then
		assertEquals(0, patients.size());
	}

	/**
	 * QueryByExampleExecutor: Uses QueryByExampleExecutor.exists() to check record exists by Example
	 */
	@Test
	public void testExistsUsingQBEByFirstName() {
		// when
		// create probe to be passed to Example
		Patient patient = new Patient();
		patient.setFirstName(UUID.randomUUID().toString());

		// when
		Example<Patient> example = Example.of(patient);
		boolean isExistsByFirstName = patientRepository.exists(example); // check record exists by passed Example

		// then
		assertFalse(isExistsByFirstName);
	}

	/**
	 * QueryByExampleExecutor: Creates ExampleMatcher find all by firstName and uses QueryByExampleExecutor.findAll(Example, Sort) version to sort records.
	 */
	@Test
	public void testFindAllUsingQBEByFirstNameStartsWitSortByFirstNameAscAndLastNameDesc() {
		// when
		// create probe to be passed to Example
		Patient patient = new Patient();
		patient.setFirstName(UUID.randomUUID().toString());

		// when
		Example<Patient> example = Example.of(patient); 
		List<Patient> patients = patientRepository.findAll(example, Sort.by(Order.asc("firstName"), Order.desc("lastName"))); // with sorting

		// then
		assertEquals(0, patients.size());
	}
	
	/**
	 * CrudRepository: Save	
	 */
	@Test
	public void testSave() {
		// given 
		Patient patient = createTestPatient();

		// when
		patientRepository.save(patient);

		// then
		assertNotNull(patient.getId());
		assertTrue(patientRepository.existsById(patient.getId()));
	}

	private Patient createTestPatient() {
		Patient patient = new Patient();
		patient.setFirstName("Bob");
		patient.setLastName("Sargent");
		Calendar dob = Calendar.getInstance();
		dob.add(Calendar.YEAR, -20);
		patient.setDob(dob);
		return patient;
	}
}
