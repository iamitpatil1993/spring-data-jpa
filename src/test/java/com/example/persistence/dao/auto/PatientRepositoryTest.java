package com.example.persistence.dao.auto;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.persistence.BaseTest;
import com.example.persistence.model.Patient;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // just for readability of logs (queries executed by test cases)
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
	 * CrudRepository: Save, existsById
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
	
	/**
	 * CrudRepository: count -> Finds count of all entities in database for given entity type
	 */
	@Test
	public void testCount() {
		// given
		Patient patient = createTestPatient();
		patientRepository.save(patient);
		
		// when
		long count = patientRepository.count();
		
		// then
		assertTrue(count > 0);
	}

	/**
	 * CrudRepository: delete
	 */
	@Test
	public void testDelete() {
		// given
		Patient patient = createTestPatient();
		patientRepository.save(patient); // save called transaction created and completed, so this returned entity is
		// detached, rather there is no persitence context here.
		// (It will be there when we will be using service layer to demarcate the
		// transactions)

		// when
		patientRepository.delete(patient); // as we know, in order to delete entity, entity must be managed in current
		// persistence context. In this case, there is no
		// transaction prior to calling this delete method, so delete method starts NEW
		// transaction (so new persistence context created), in which our entity is not
		// managed.
		// so, CrudRepository.delete() implementation SimpleJpaRepository first check
		// entity is managed in current persistence context, if no then it first fetch
		// the
		// entity to make it managed in current persistence context and then deletes it.
		// so, we don't have to worry about, ensuring entity we are passing to delete
		// needs to be managed, spring handles it.

		// then
		assertFalse(patientRepository.findById(patient.getId()).isPresent());
	}
	
	/**
	 * CrudRepository: deleteAll
	 */
	@Test
	public void testDeleteAll() {
		// given
		Patient patient = createTestPatient();
		patientRepository.save(patient);

		// when
		patientRepository.deleteAll(); // so much expensive, first it finds all entities, then it calls
										// CrudRepository.delete() in loop for all.
		// so it does not deletes all entities using sql/named query in order to sync data with
		// persistence context.
		// so, queries executed are (n + 1) minimum, where n is number of records in
		// database in best case. In worst case if entities are not managed,
		// will be (1 + 2n)

		// then
		assertEquals(0, patientRepository.count());
	}
	
	/**
	 * CrudRepository: deleteAll(Iterable<T> entities), findAllById(Iterable<T> ids)
	 */
	@Test
	public void testDeleteAllIterable() {
		// given
		Patient patient = createTestPatient();
		patientRepository.save(patient);
		
		Patient anotherPatient = createTestPatient();
		patientRepository.save(anotherPatient);

		// when
		patientRepository.deleteAll(Arrays.asList(patient, anotherPatient)); // calls CrudRepository.delete(Entity) in iteration.

		// then
		assertThat(patientRepository.findAllById(Arrays.asList(patient.getId(), anotherPatient.getId())), empty());
	}
	
	/**
	 * CrudRepository: deleteById(ID<T> id), findById(ID id)
	 */
	@Test
	public void testDeleteByIdWithEntityExistsById() {
		// given
		Patient patient = createTestPatient();
		patientRepository.save(patient);

		// when
		patientRepository.deleteById(patient.getId()); // first finds entity and then delete it

		// then
		assertThat(patientRepository.findById(patient.getId()).isPresent(), is(false));
	}

	/**
	 * CrudRepository: deleteById(ID<T> id), findById(ID id)
	 */
	@Test(expected = EmptyResultDataAccessException.class)
	public void testDeleteByIdWithNotEntityExistsById() {
		// given
		Integer patientId = 12321;

		// when
		patientRepository.deleteById(patientId); // throws EmptyResultDataAccessException, since entity does not exists by ID
	}
	
	/**
	 * CrudRepository: saveAll(iterable<T> entities)
	 */
	@Test
	public void testSaveAll() {
		// given
		Patient patient = createTestPatient();
		Patient patient1 = createTestPatient();
		Patient patient2 = createTestPatient();
		;

		// when
		patientRepository.saveAll(Arrays.asList(patient, patient1, patient2)); // calls CrudRepository.save in
		// iteration.
		// it manages transaction by default as repository methods are Transactional by
		// default.

		// then
		assertThat(patientRepository.findAllById(Arrays.asList(patient.getId(), patient1.getId(), patient2.getId())),
				hasSize(3));
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
