package com.example.persistence.dao.auto;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.hibernate.LazyInitializationException;
import org.junit.After;
import org.junit.Before;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.concurrent.ListenableFuture;

import com.example.persistence.BaseTest;
import com.example.persistence.dao.PatientVitalRepository;
import com.example.persistence.dao.projections.PatientNameProjection;
import com.example.persistence.dao.projections.PatientProjection;
import com.example.persistence.dto.PatientNameDto;
import com.example.persistence.model.Gender;
import com.example.persistence.model.Patient;
import com.example.persistence.model.PatientVital;
import com.example.persistence.model.QPatient;
import com.example.persistence.model.VitalType;
import com.example.persistence.service.PatientService;
import com.example.persistence.util.Utils;

@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // just for readability of logs (queries executed by test cases)
public class PatientRepositoryTest extends BaseTest {

	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private PatientVitalRepository patientVitalRepository;
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private Utils utils;
	
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
		patientVitalRepository.deleteAll();
		patientRepository.deleteAll(); // so much expensive, first it finds all entities, then it calls
										// CrudRepository.delete() in loop for all.
		// so it does not deletes all entities using sql/named query in order to sync data with
		// persistence context.
		// so, queries executed are (n + 1) minimum, where n is number of records in
		// database in best case. In worst case if entities are not managed,
		// will be (1 + 2n)
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
		patientVitalRepository.deleteAll();
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
	
	/**
	 * PagingAndSortingRepository: findAll(Pageable)
	 */
	@Test
	public void testFindAllWithPagination() {
		// given
		int pageNumber = 0;
		int pageSize = 5;
		
		// we can use same pageable object in our custom queries as well.
		Pageable pageable = PageRequest.of(pageNumber, pageSize,
				Sort.by(Order.asc("firstName").nullsLast(), Order.asc("lastName").nullsLast()));

		// when
		Page<Patient> page = null;
		do {
			page = patientRepository.findAll(pageable);
			pageable = pageable.next();
		} while (page.hasNext());
	}
	
	/**
	 * JpaRepository: flush()
	 */
	@Test
	public void testFlush() {
		// when
		patientRepository.flush(); // flushes/synchronizes any pending changes in current persistence context to
									// database.
		// Current here no persistence context, so not a single query will get executed
		// here by flush operation.
	}
	
	/**
	 * JpaRepository: saveAndFlush()
	 */
	@Test
	public void testSaveAndFlush() {
		// given
		Patient patient = createTestPatient();

		// when
		patientRepository.saveAndFlush(patient); // Saves passed object and then flushes/synchronizes any pending
													// changes in current persistence context to database.
		// it just calls CrudRepository.save() and then JpaRepository.flush()
		assertNotNull(patient.getId());
		assertTrue(patientRepository.existsById(patient.getId()));
	}	

	/**
	 * JpaRepository: deleteAllInBatch -> Deletes all records in single batch (i.e
	 * query) opposed to CrudRepository.deleteAll() which deletes records in
	 * Iteration. So, this version is so much optimized and should be used over one
	 * in CrudRepository.
	 * 
	 * NOTE: It only deletes the records in database, but does not detach the
	 * entities from current persistence context
	 */
	@Test
	public void testDeleteAllInBatch() {
		// when
		patientService.doSomethingInTransaction();
	}	
	
	/**
	 * JpaRepository: deleteInBatch -> Deletes records in single batch (i.e
	 * query) opposed to CrudRepository.deleteAll() which deletes records in
	 * Iteration. So, this version is so much optimized and should be used over one
	 * in CrudRepository.
	 * 
	 * NOTE: It only deletes the records in database, but does not detach the
	 * entities from current persistence context
	 */
	@Test
	public void testDeleteInBatch() {
		// given
		Patient patient = createTestPatient();
		Patient anotherPatient = createTestPatient();

		// when
		patientService.deletePatientsInBatch(Arrays.asList(patient, anotherPatient));

		// then
		assertThat(patientRepository.findAllById(Arrays.asList(patient.getId(), anotherPatient.getId())), empty());
	}	
	
	/**
	 * JpaRepository: getOne -> counterpart of EntityManager.getreference(), which
	 * returns proxy with only primary key and does not executes any query to fetch
	 * entity state from database. Entity state is fetched from database on first
	 * access.
	 */
	@Test(expected = LazyInitializationException.class)
	public void testGetOne() {
		// given
		Patient patient = createTestPatient();
		patientRepository.save(patient);
		
		// when
		Patient proxyPatient = patientRepository.getOne(patient.getId()); // does not execute anything on database
		assertThat(proxyPatient, is(instanceOf(Patient.class)));
		assertNull(proxyPatient.getFirstName()); // this will fail due to LaziInitializationError, since here we do not have transaction and persistence unit here.
	}

	/**
	 * Query Methods:1 -> Finds patient only by ssn exact match
	 */
	@Test
	public void testFindPatientBySsn() {
		// given
		String ssn = UUID.randomUUID().toString();

		// when
		Patient patient = patientRepository.findPatientBySsn(ssn);

		// then
		assertNull(patient);
	}

	/**
	 * Query Methods:2 -> Finds patient only by ssn with ignoring case
	 */
	@Test
	public void testReadAnyPatienttBySsnIgnoreCase() {
		// given
		String ssnSearchString = UUID.randomUUID().toString();

		// when
		Optional<Patient> patient = patientRepository.readAnyPatienttBySsnIgnoreCase(ssnSearchString);

		// then
		assertNotNull(patient);
		assertFalse(patient.isPresent());
	}

	/**
	 * Query Methods:3 -> Finds patient by firstName or lastName with exact match
	 * 
	 */
	@Test
	public void testGetByFirstNameOrLastName() {
		// given
		String firstName = UUID.randomUUID().toString();
		String lastName = UUID.randomUUID().toString();

		// when
		Optional<Patient> patient = patientRepository.getByFirstNameOrLastName(firstName, lastName);

		// then
		assertNotNull(patient);
		assertFalse(patient.isPresent());
	}

	/**
	 * Query Methods:4 -> Finds patient by firstName And lastName with exact match
	 * 
	 */
	@Test
	public void testGetByFirstNameAndLastName() {
		// given
		Patient patient = createTestPatient();
		String firstName = "dummy_first_name".concat(UUID.randomUUID().toString());
		patient.setFirstName(firstName);
		patientRepository.save(patient);

		// when
		Patient patientSearchResult = patientRepository.getByFirstNameAndLastName(patient.getFirstName(),
				patient.getLastName());

		// then
		assertNotNull(patientSearchResult);
		assertEquals(patient.getId(), patientSearchResult.getId());
	}

	/**
	 * Query Methods:5 -> Finds patient by firstName And lastName with first name
	 * ignoring case
	 * 
	 */
	@Test
	public void testGetByFirstNameIgnoreCaseAndLastName() {
		// given
		Patient patient = createTestPatient();
		String firstName = "dummy_first_name".concat(UUID.randomUUID().toString());
		patient.setFirstName(firstName);
		patientRepository.save(patient);

		// when
		Patient patientSearchResult = patientRepository.getByFirstNameIgnoreCaseAndLastName(firstName.toUpperCase(),
				patient.getLastName());

		// then
		assertNotNull(patientSearchResult);
		assertEquals(patient.getId(), patientSearchResult.getId());
	}

	/**
	 * Query Methods:6 -> Finds patient by firstName And lastName all conditions
	 * ignoring case.
	 * 
	 */
	@Test
	public void testFindByFirstNameAndLastNameAllIgnoringCase() {
		// given
		Patient patient = createTestPatient();
		String firstName = "dummy_first_name".concat(UUID.randomUUID().toString());
		patient.setFirstName(firstName);

		String lastName = "dummy_last_name".concat(UUID.randomUUID().toString());
		patient.setLastName(lastName);
		patientRepository.save(patient);

		// when
		Patient patientSearchResult = patientRepository
				.findByFirstNameAndLastNameAllIgnoringCase(firstName.toUpperCase(), lastName.toLowerCase());

		// then
		assertNotNull(patientSearchResult);
		assertEquals(patient.getId(), patientSearchResult.getId());
	}

	/**
	 * Query Methods:7 -> Finds 'Distinct'patient by firstName
	 */
	@Test
	public void testFindDistinctPatientByFirstName() {
		// given
		Patient patient = createTestPatient();
		String firstName = "dummy_first_name".concat(UUID.randomUUID().toString());
		patient.setFirstName(firstName);
		patientRepository.save(patient);

		// when
		Patient patientSearchResult = patientRepository.findDistinctPatientByFirstName(firstName);

		// then
		assertNotNull(patientSearchResult);
		assertEquals(patient.getId(), patientSearchResult.getId());
	}

	/**
	 * Query Methods:8 -> Finds patients (possibly multple) by dob
	 */
	@Test
	public void testFindByDob() {
		// given
		Patient patient = createTestPatient();
		Calendar dob = Calendar.getInstance();
		dob.add(Calendar.YEAR, -15);

		patient.setDob(dob);
		patientRepository.save(patient);

		Patient anotherPatient = createTestPatient();
		dob = Calendar.getInstance();
		dob.add(Calendar.YEAR, -20);

		anotherPatient.setDob(dob);
		patientRepository.save(anotherPatient);

		// when
		List<Patient> patientSearchResults = patientRepository.findByDob(patient.getDob());

		// then
		assertNotNull(patientSearchResults);
		assertThat(patientSearchResults.size(), is(greaterThan(0)));
		List<Integer> patientSearchResultIds = patientSearchResults.stream().map(Patient::getId)
				.collect(Collectors.toList());
		assertThat(patientSearchResultIds, contains(patient.getId()));
		assertThat(patientSearchResultIds, not(contains(anotherPatient.getId())));
	}

	/**
	 * Query Methods:9 -> Finds patients (possibly multiple) by dob after
	 */
	@Test
	public void testFindByDobIsAfter() {
		// given
		Patient patient = createTestPatient();
		Calendar dob = Calendar.getInstance();
		dob.add(Calendar.YEAR, -1);

		patient.setDob(dob);
		patientRepository.save(patient);

		Patient anotherPatient = createTestPatient();
		dob = Calendar.getInstance();
		dob.add(Calendar.YEAR, -2);

		anotherPatient.setDob(dob);
		patientRepository.save(anotherPatient);

		// when
		Calendar criteriaDob = Calendar.getInstance();
		criteriaDob.add(Calendar.YEAR, -3);
		List<Patient> patientSearchResults = patientRepository.findByDobIsAfter(criteriaDob);

		// then
		assertNotNull(patientSearchResults);
		assertThat(patientSearchResults.size(), is(greaterThanOrEqualTo(2)));
		List<Integer> patientSearchResultIds = patientSearchResults.stream().map(Patient::getId)
				.collect(Collectors.toList());
		assertTrue(patientSearchResultIds.contains(patient.getId()));
		assertTrue(patientSearchResultIds.contains(anotherPatient.getId()));
	}

	/**
	 * Query Methods:10 -> Finds patients (possibly multiple) by dob between two
	 * dates
	 */
	@Test
	public void testFindByDobIsBetween() {
		// given
		Patient patient = createTestPatient();
		Calendar dob = Calendar.getInstance();
		dob.set(2015, 7, 20);

		patient.setDob(dob);
		patientRepository.save(patient);

		Patient anotherPatient = createTestPatient();
		dob = Calendar.getInstance();
		dob.set(2015, 7, 21);

		anotherPatient.setDob(dob);
		patientRepository.save(anotherPatient);

		// when
		Calendar equalToOrAfterDate = Calendar.getInstance();
		equalToOrAfterDate.set(2015, 7, 20);

		Calendar beforeOrEqualDate = Calendar.getInstance();
		beforeOrEqualDate.set(2015, 7, 21);

		List<Patient> patientSearchResults = patientRepository.findByDobIsBetween(equalToOrAfterDate,
				beforeOrEqualDate);

		// then
		assertNotNull(patientSearchResults);
		assertThat(patientSearchResults.size(), is(greaterThanOrEqualTo(2)));
		List<Integer> patientSearchResultIds = patientSearchResults.stream().map(Patient::getId)
				.collect(Collectors.toList());
		assertTrue(patientSearchResultIds.contains(patient.getId()));
		assertTrue(patientSearchResultIds.contains(anotherPatient.getId()));
	}

	/**
	 * Query Methods:11 -> Finds patients count with null ssn
	 */
	@Test
	public void testCountBySsnIsNull() {
		// given
		Patient patientWithoutSsn = createTestPatient();
		patientWithoutSsn.setSsn(null);
		patientRepository.save(patientWithoutSsn);

		// when
		int patientsWithNullSsnCount = patientRepository.countBySsnIsNull();

		// then
		assertThat(patientsWithNullSsnCount, is(greaterThanOrEqualTo(1)));
	}

	/**
	 * Query Methods:12 -> Finds patients by blood group in provided blood group
	 * list
	 */
	@Test
	public void testFindPatientsByBloodGroupIsIn() {
		// given
		List<String> requireBloodGroups = Arrays.asList("O-", "A-");
		Patient patient = createTestPatient();
		patient.setBloodGroup(requireBloodGroups.get(0));
		patientRepository.save(patient);

		Patient anotherPatient = createTestPatient();
		anotherPatient.setBloodGroup(requireBloodGroups.get(1));
		patientRepository.save(anotherPatient);

		// when

		List<Patient> patientsSearchResults = patientRepository.findPatientsByBloodGroupIsIn(requireBloodGroups);

		// then
		assertThat(patientsSearchResults, notNullValue());
		assertThat(patientsSearchResults.size(), is(greaterThanOrEqualTo(2)));

		List<Integer> patientsSearchResultsIds = patientsSearchResults.stream().map(Patient::getId)
				.collect(Collectors.toList());
		assertThat(patientsSearchResultsIds, hasItems(patient.getId(), anotherPatient.getId()));

		Set<String> patientsSearchResultsBloodGroups = patientsSearchResults.stream().map(Patient::getBloodGroup)
				.collect(Collectors.toSet());
		assertThat(patientsSearchResultsBloodGroups.size(), is(equalTo(2)));
	}

	/**
	 * Query Methods:13 -> Finds patients firstname or lastname starting with search
	 * string
	 */
	@Test
	public void testFindPatientsByFirstNameStartingWithOrLastNameStartingWithOrderByFirstNameAscLastNameAsc() {
		Patient anotherPatient = createTestPatient();
		anotherPatient.setFirstName("test_f_name");
		patientRepository.save(anotherPatient);

		Patient patient = createTestPatient();
		patient.setFirstName("test_l_name");
		patientRepository.save(patient);
		String nameSearchString = "test_f";

		// when
		List<Patient> patientsSearchResults = patientRepository
				.findPatientsByFirstNameStartingWithOrLastNameStartingWithOrderByFirstNameAscLastNameAsc(
						nameSearchString, nameSearchString);

		// then
		assertThat(patientsSearchResults, notNullValue());
		assertThat(patientsSearchResults.size(), is(greaterThanOrEqualTo(1)));
		assertThat(patientsSearchResults.stream().map(Patient::getId).collect(Collectors.toList()),
				contains(anotherPatient.getId()));
	}
	
	/**
	 * Custom Query Methods:1 -> Finds all patients with null ssn string
	 */
	@Test
	public void testFindPatientWithNullSsn() {
		// when
		List<Patient> patientsSearchResults = patientRepository.findPatientWithNullSsn();

		// then
		assertThat(patientsSearchResults, notNullValue());
	}

	/**
	 * Custom Query Methods:2 -> Finds all patients IDs with null ssn string
	 */
	@Test
	public void testFindAllPatientIdsWithNullSsn() {
		// when
		List<Integer> patientsSearchResults = patientRepository.findAllPatientIdsWithNullSsn();

		// then
		assertThat(patientsSearchResults, notNullValue());
	}

	/**
	 * Custom Query Methods:3 -> Finds all patients with not a single vital reported
	 * string
	 */
	@Test
	public void testFindAllPatientsWithoutAnyVital() {
		Patient patient = createTestPatient();
		patientRepository.save(patient);

		// when
		List<Patient> patientsSearchResults = patientRepository.findAllPatientsWithoutAnyVital();

		// then
		assertThat(patientsSearchResults, notNullValue());
		assertThat(patientsSearchResults.size(), is(greaterThanOrEqualTo(1)));
	}

	/**
	 * Custom Query Methods:4 -> Finds all patients vitals string
	 */
	@Test
	public void testFindPatientVitals() {
		Patient patient = createTestPatient();
		patientRepository.save(patient);

		PatientVital patientVital = new PatientVital();
		patientVital.setPatient(patient);
		patientVital.setValue(62d);
		patientVital.setVital(VitalType.PULSE);

		patientVitalRepository.save(patientVital);

		// when
		List<PatientVital> patientsSearchResults = patientRepository.findPatientVitals();

		// then
		assertThat(patientsSearchResults, notNullValue());
		assertThat(patientsSearchResults.size(), is(greaterThanOrEqualTo(1)));
	}

	/**
	 * Custom Query Methods:5 -> Finds all patients where isDeleted = false (using
	 * native query) string
	 */
	@Test
	public void testFindAllPatientsWithIsDeletedFalse() {
		// when
		List<Patient> patientsSearchResults = patientRepository.findAllPatientsWithIsDeletedFalse();

		// then
		assertThat(patientsSearchResults, notNullValue());
	}

	/**
	 * Custom Query Methods:6 -> Finds all patients with firstName or lastName using
	 * positional parameters string
	 */
	@Test
	public void testFindAllWithFirstNameOrLastNameUsingPositionalParameters() {
		// given
		String firstName = UUID.randomUUID().toString();
		String lastName = UUID.randomUUID().toString();

		// when
		Sort sortOrder = Sort.by(Order.asc("firstName"), Order.asc("lastName")); // we can specify only properties for
		// sorting using Sort
		List<Patient> patientsSearchResults = patientRepository
				.findAllWithFirstNameOrLastNameUsingPositionalParameters(firstName, lastName, sortOrder);

		// then
		assertThat(patientsSearchResults, notNullValue());
		assertThat(patientsSearchResults.size(), is(0));
	}

	/**
	 * Custom Query Methods:7 -> Finds all patients with firstName or lastName using
	 * named parameters, Sort by names string
	 */
	@Test
	public void testFindAllWithFirstNameOrLastNameUsingNamedParameters() {
		// given
		String firstName = UUID.randomUUID().toString();
		String lastName = UUID.randomUUID().toString();

		// when
		Sort sortOrder = Sort.by(Order.asc("firstName"), Order.asc("lastName")); // we can specify only properties for
		// sorting using Sort
		List<Patient> patientsSearchResults = patientRepository
				.findAllWithFirstNameOrLastNameUsingNamedParameters(firstName, lastName, sortOrder);

		// then
		assertThat(patientsSearchResults, notNullValue());
		assertThat(patientsSearchResults.size(), is(0));
	}

	/**
	 * Custom Query Methods:8 -> Finds all patients with firstName or lastName using
	 * named parameters, Sort by length of name string
	 */
	@Test
	public void testFindAllWithFirstNameOrLastNameUsingNamedParametersWithSoryBylengthOfName() {
		// given
		String firstName = UUID.randomUUID().toString();
		String lastName = UUID.randomUUID().toString();

		// when
		Sort sortOrder = JpaSort.unsafe("LENGTH(CONCAT(firstName, lastName))"); // we can use JpaSort.unsafe() to sorty
		// by anything(not only entity
		// properties)
		List<Patient> patientsSearchResults = patientRepository
				.findAllWithFirstNameOrLastNameUsingNamedParameters(firstName, lastName, sortOrder);

		// then
		assertThat(patientsSearchResults, notNullValue());
		assertThat(patientsSearchResults.size(), is(0));
	}

	/**
	 * Custom Query Methods:9 -> Finds all patients with pagination and sorting
	 * string
	 */
	@Test
	public void testFindAllPatients() {
		// when
		Sort sortOrder = Sort.by(Order.desc("updatedDate")); // pagination and sort both can not be passed to
		Pageable pageable = PageRequest.of(0, 20, sortOrder);
		List<Patient> patientsSearchResults = patientRepository.findAllPatients(pageable);

		// then
		assertThat(patientsSearchResults, notNullValue());
	}

	/**
	 * Custom Query Methods:10 -> Finds all patients using native quey with
	 * pagination and sorting string
	 */
	@Test
	public void testFindAllPatientsUsingNativeQueryAndPagination() {
		// when
		Sort sortOrder = Sort.by(Order.desc("updated_date")); // pagination and sort both can not be passed to
		Pageable pageable = PageRequest.of(1, 20, sortOrder);
		Page<Patient> patientsSearchResults = patientRepository.findAllPatientsUsingNativeQueryAndPagination(pageable);

		// then
		assertThat(patientsSearchResults, notNullValue());
	}

	/**
	 * Custom Query Methods:11 -> Finds all patients with given blood groups string
	 */
	@Test
	public void testFindAllByBloodGroup() {
		// when
		List<String> bloodGroups = Arrays.asList("O-", "A-");
		List<Patient> patientsSearchResults = patientRepository.findAllByBloodGroup(bloodGroups);

		// then
		assertThat(patientsSearchResults, notNullValue());
	}

	/**
	 * Custom Query Methods:12 -> Update patient ssn by patientId string
	 */
	@Test
	public void testUpdateSsnByPatientId() {
		// given
		Patient patientEntity = createTestPatient();
		patientEntity.setSsn(null);
		patientRepository.save(patientEntity);

		// when
		patientEntity.setSsn(UUID.randomUUID().toString());
		int updatedRecordCount = patientService.updatePatientSsnByPatienId(patientEntity);

		// then
		assertThat(updatedRecordCount, is(1));
		Optional<Patient> updatedPatient = patientRepository.findById(patientEntity.getId());

		assertThat(updatedPatient.isPresent(), is(true));
		assertEquals(updatedPatient.get().getSsn(), patientEntity.getSsn());
	}

	/**
	 * Custom Repository:1 -> Custom repository method to get all Diabetic patients.
	 */
	@Test
	public void testFindAllDiabeticPatients() {
		Patient patient = createTestPatient();
		patientRepository.save(patient);
		
		PatientVital patientVital = new PatientVital();
		patientVital.setPatient(patient);
		patientVital.setVital(VitalType.SUGAR);
		patientVital.setValue(300d);
		patientVitalRepository.save(patientVital);
		
		// when
		List<Patient> diabeticPatients = patientRepository.findAllDiabeticPatients();
		
		assertThat(diabeticPatients.size(), is(greaterThan(0)));
		assertThat(diabeticPatients.stream().map(Patient::getId).collect(Collectors.toList()), hasItems(patient.getId()));
	}
	
	/**
	 * Query Method: -> Delete by Id
	 */
	@Test
	public void testDeleteById() {
		// given
		Patient patient = createTestPatient();
		patientRepository.save(patient);

		// when
		patientRepository.deleteById(patient.getId());

		// then
		assertThat(patientRepository.findById(patient.getId()).isPresent(), is(false));
	}

	/**
	 * Query Method: -> Remove all patients by blood group
	 */
	@Test
	public void testremovePatientByBloodGroup() {
		String bloodGroup = "O-";
		Patient patient = createTestPatient();
		patient.setBloodGroup(bloodGroup);
		patientRepository.save(patient);

		// when
		patientVitalRepository.deleteAllByPatientsWithBloodGroup(bloodGroup); // deleting for Foreign key contrain
		// violation issue
		patientRepository.removePatientByBloodGroup(bloodGroup);
		
		// then
		assertThat(patientRepository.findAllByBloodGroup(Arrays.asList(bloodGroup)), is(empty()));
	}
	
	/**
	 * Null handling in spring - 1: Throws IllegalArgumentException if argument passed is null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetBySsWithNullSsnPassed() {
		// given
		String ssn = null;
		
		// when
		patientRepository.getBySsn(ssn);
	}
	
	/**
	 * Null handling in spring - 2: Throws EmptyResultDataAccessException if return value is null.
	 */
	@Test(expected = EmptyResultDataAccessException.class)
	public void testGetBySsnWithNoRecordFoundBySsn() {
		// given
		String ssn = UUID.randomUUID().toString();
		
		// when
		patientRepository.getBySsn(ssn);
	}
	
	/**
	 * Null handling in spring - 3: Allows null value to passed as a argument due to @Nullable.
	 */
	@Test
	public void testReadBySsWithNullSsnPassed() {
		// given
		String ssn = null;
		
		// when
		Patient patient = patientRepository.readBySsn(ssn);
		
		// then
		assertNull(patient);
	}
	
	/**
	 * Null handling in spring - 4: Allows null value returned due to @Nullable.
	 */
	@Test
	public void testReadBySsWithNoRecordFoundBySsn() {
		// given
		String ssn = UUID.randomUUID().toString();
		
		// when
		Patient patient = patientRepository.readBySsn(ssn);
		
		// then
		assertNull(patient);
	}
	
	/**
	 * Null handling in spring - 5: Using Optional to allow null no result avaiable case
	 */
	@Test
	public void testFindBySsWithNoRecordFoundBySsn() {
		// given
		String ssn = UUID.randomUUID().toString();
		
		// when
		Optional<Patient> patient = patientRepository.findBySsn(ssn);
		
		// then
		assertNotNull(patient);
		assertFalse(patient.isPresent());
	}
	
	/**
	 * Query Method: -> Conditions on nested property (via associations)
	 */
	@Test
	public void testFindDistinctPatientByVitalsVitalAndVitalsValueIsGreaterThan() {
		// given
		VitalType vitalType = VitalType.SUGAR;
		Patient patient = createTestPatient();
		patientRepository.save(patient);
		
		PatientVital patientVital = new PatientVital();
		patientVital.setPatient(patient);
		patientVital.setVital(VitalType.SUGAR);
		patientVital.setValue(320d);
		patientVitalRepository.save(patientVital);
		
		// when
		List<Patient> diabeticPatients = patientRepository.findDistinctPatientByVitalsVitalAndVitalsValueIsGreaterThan(vitalType, 300d);
	
		assertThat(diabeticPatients, is(not(empty())));
	}
	
	
	/**
	 * Query Method: -> Find Top/first one record
	 */
	@Test
	public void testFindTopPatientByVitalsVitalAndVitalsValueIsGreaterThan() {
		// given
		VitalType vitalType = VitalType.SUGAR;
		Patient patient = createTestPatient();
		patientRepository.save(patient);

		Patient anotherPatient = createTestPatient();
		patientRepository.save(anotherPatient);

		PatientVital patientVital = new PatientVital();
		patientVital.setPatient(patient);
		patientVital.setVital(VitalType.SUGAR);
		patientVital.setValue(320d);
		patientVitalRepository.save(patientVital);

		patientVital.setId(null);
		patientVital.setPatient(anotherPatient);
		patientVital.setValue(400d);
		patientVitalRepository.save(patientVital);

		// when
		List<Patient> diabeticPatients = patientRepository
				.findTopPatientByVitalsVitalAndVitalsValueIsGreaterThanOrderByVitalsValueDesc(vitalType, 300d);

		assertThat(diabeticPatients, is(not(empty())));
		assertThat(diabeticPatients.size(), is(1));
	}
	
	/**
	 * Query Method: -> Find Top/first one record using Optional
	 */
	@Test
	public void testFindTopByVitalsVitalAndVitalsValueIsGreaterThanOrderByVitalsValueDesc() {
		// given
		VitalType vitalType = VitalType.SUGAR;
		Patient patient = createTestPatient();
		patientRepository.save(patient);

		Patient anotherPatient = createTestPatient();
		patientRepository.save(anotherPatient);

		PatientVital patientVital = new PatientVital();
		patientVital.setPatient(patient);
		patientVital.setVital(VitalType.SUGAR);
		patientVital.setValue(320d);
		patientVitalRepository.save(patientVital);

		patientVital.setId(null);
		patientVital.setPatient(anotherPatient);
		patientVital.setValue(400d);
		patientVitalRepository.save(patientVital);

		// when
		Optional<Patient> diabeticPatient = patientRepository
				.findTopByVitalsVitalAndVitalsValueIsGreaterThanOrderByVitalsValueDesc(vitalType, 300d);

		assertThat(diabeticPatient, is(notNullValue()));
		assertThat(diabeticPatient.isPresent(), is(true));
	}
	
	/**
	 * Query Method: -> Find Top/first one record using Optional
	 */
	@Test
	public void testFindTop3ByVitalsVitalAndVitalsValueIsGreaterThanOrderByVitalsValueDesc() {
		// given
		VitalType vitalType = VitalType.SUGAR;
		for (int i = 0; i < 10; i++) {
			Patient patient = createTestPatient();
			patientRepository.save(patient);

			PatientVital patientVital = new PatientVital();
			patientVital.setPatient(patient);
			patientVital.setVital(VitalType.SUGAR);
			patientVital.setValue(320d + (i * 2));
			patientVitalRepository.save(patientVital);
		}
		
		// when
		Pageable pageable = PageRequest.of(0, 5);
		Page<Patient> diabeticPatients = patientRepository
				.findTop3ByVitalsVitalAndVitalsValueIsGreaterThanOrderByVitalsValueDesc(vitalType, 300d, pageable);

		assertThat(diabeticPatients.getTotalElements(), is(3l));
		assertThat(diabeticPatients.getContent().size(), is(3));
	}

	@Test
	public void testFindByFirstName() {
		// when
		ListenableFuture<List<Patient>> future = patientRepository.findAllPatientss(PageRequest.of(0, 100000));
		
		// then
		future.addCallback(data -> {
			System.out.println("result is :: " + data.size());
		}, (e) -> {
			e.printStackTrace();
		});
		System.out.println("returning");
	}
	
	@Test
	public void testDoSomething() {
		// when
		patientRepository.doSomething();
	}
	
	@Test
	public void testDoSomethingOnPatientVitalRepository() {
		// when
		patientVitalRepository.doSomething();
	}

	@Test
	public void testFindByPatientIdUsingQueryDsl() {
		// given
		Patient patient = createTestPatient();
		patientRepository.save(patient);
		
		// when
		Optional<Patient> patientOptional = patientRepository.findByPatientIdUsingQueryDsl(patient.getId());
		
		// then
		assertThat(patientOptional, is(notNullValue()));
		assertThat(patientOptional.isPresent(), is(true));
		assertThat(patientOptional.get().getId(), equalTo(patient.getId()));
	}
	
	@Test
	public void testGetPatientsCreatedInLastMonth() {
		// when
		List<Patient> patients = patientRepository.getPatientsCreatedInLastMonth();
		
		// then
		assertThat(patients, is(notNullValue()));
	}
	
	@Test
	public void testFindYungestPatient() {
		// when
		Optional<Calendar> dobOfYungestPatient = patientRepository.findYungestPatient();
		
		// then
		assertThat(dobOfYungestPatient, is(notNullValue()));
	}
	
	@Test
	public void testFindYungestPatientGroupByBloodGroup() {
		// given
		Patient patient = createTestPatient();
		patient.setBloodGroup("O-");
		patientRepository.save(patient);
		
		patient = createTestPatient();
		patient.setBloodGroup("O-");
		patientRepository.save(patient);
		
		patient = createTestPatient();
		patient.setBloodGroup("O+");
		patientRepository.save(patient);

		// when
		Map<String, Calendar> dobOfYungestPatient = patientRepository.findYungestPatientGroupByBloodGroup();

		// then
		assertThat(dobOfYungestPatient, is(notNullValue()));
		assertThat(dobOfYungestPatient.size(), is(2));
	}
	
	/**
	 * Query DSL with spring QuerydslPredicateExecutor. 
	 */
	@Test
	public void testCountUsingQueryDsl() {
		// given
		String bloodGroup = "O+";
		Patient patient = createTestPatient();
		patient.setBloodGroup(bloodGroup);
		patientRepository.save(patient);
		
		// when
		// we just need to provide predicate and spring will manage other details.
		// if we want more customized query, we can use QueryDsl native querying using custom repository implememntation.
		long countOfPatientByBloodGroup = patientRepository.count(QPatient.patient.bloodGroup.eq(bloodGroup));
		boolean isExistsByBloodGroup = patientRepository.exists(QPatient.patient.bloodGroup.eq(bloodGroup));
		
		// then
		assertThat(countOfPatientByBloodGroup, is(greaterThanOrEqualTo(1l)));
		assertThat(isExistsByBloodGroup, is(true));
	}
	
	@Test
	public void testFindAllByBloodGroupUsingNamedQueryAutoDetectionByNamingConvention() {
		// given
		String bloodGroup = "O-";
		Patient patient = createTestPatient();
		patient.setBloodGroup(bloodGroup);
		patientRepository.save(patient);
		
		// when
		List<Patient> patientsWithBloodGroup = patientRepository.findAllByBloodGroup(bloodGroup);
		
		// then
		assertThat(patientsWithBloodGroup, is(notNullValue()));
		assertThat(patientsWithBloodGroup.size(), is(greaterThanOrEqualTo(1)));
		assertThat(patientsWithBloodGroup.stream().map(Patient::getId).collect(Collectors.toList()), hasItems(patient.getId()));
	}
	
	/**
	 * Query defined at Query method in repository overrides matching named query. In this case,
	 * Named query 'Patient.findAllHypertensionPatients' matches to {@link PatientRepository#findAllHypertensionPatients()}.
	 * But we have defined query at {@link PatientRepository#findAllHypertensionPatients()} with no isDeleted preficates in where clause.
	 * Hence query method will also get isDeleted = true records, since it overrides query defined at Model {@link Patient}
	 * 
	 */
	@Test
	public void testFindAllHypertensionPatients() {
		// given
		Patient patient = createTestPatient();
		patient.setDeleted(true); // IsDeleted true
		patientRepository.save(patient);
		
		PatientVital patientVital = new PatientVital();
		patientVital.setPatient(patient);
		patientVital.setVital(VitalType.DI_BP);
		patientVital.setValue(105d);
		patientVitalRepository.save(patientVital);
		
		Patient anotherPatient = createTestPatient();
		patientRepository.save(anotherPatient);
		
		PatientVital anotherPatientVital = new PatientVital();
		anotherPatientVital.setPatient(anotherPatient);
		anotherPatientVital.setVital(VitalType.DI_BP);
		anotherPatientVital.setValue(105d);
		anotherPatientVital.setDeleted(true); // isDeleted true
		patientVitalRepository.save(anotherPatientVital);
		
		
		// when
		List<Patient> patientsWithHypertension = patientRepository.findAllHypertensionPatients();
		
		// then
		assertThat(patientsWithHypertension, is(notNullValue()));
		assertThat(patientsWithHypertension.size(), is(greaterThanOrEqualTo(2)));
		assertThat(patientsWithHypertension.stream().map(Patient::getId).collect(Collectors.toList()), hasItems(patient.getId()));
		assertThat(patientsWithHypertension.stream().map(Patient::getId).collect(Collectors.toList()), hasItems(anotherPatient.getId()));
	}
	
	@Test
	public void testGetCreatedDateById() {
		// given
		Patient patient = createTestPatient();
		patientRepository.save(patient);
		
		// when
		Optional<Calendar> createdDate = patientRepository.getCreatedDateById(patient.getId());
		
		// then
		assertNotNull(createdDate);
		assertTrue(createdDate.isPresent());
	}
	
	@Test
	public void testIsExisById() {
		// given
		Patient patient = createTestPatient();
		patientRepository.save(patient);
		
		// when
		boolean isExists = patientRepository.isExisById(patient.getId());
		boolean isExists2 = patientRepository.isExisById(Long.valueOf(Math.round(Math.random() * 50000)).intValue());
		
		// then
		assertTrue(isExists);
		assertFalse(isExists2);
	}
	
	/**
	 * Projections: Interface based open projection.
	 */
	@Test
	public void testfindPatientNames() {
		// given
		Patient patient = createTestPatient();
		patientRepository.save(patient);
		
		// when
		Optional<PatientNameProjection> patientName = patientRepository.findPatientNamesById(patient.getId());
		
		// then
		assertThat(patientName.isPresent(), is(true));
		assertThat(patientName.get().getId(), is(equalTo(patient.getId())));
		assertThat(patientName.get().getFirstName(), is(equalTo(patient.getFirstName())));
		assertThat(patientName.get().getLastName(), is(equalTo(patient.getLastName())));
	}
	
	/**
	 * Projections: Interface based close projection.
	 */
	@Test
	public void testFindPatientUsingProjectionById() {
		// given
		Patient patient = createTestPatient();
		patientRepository.save(patient);

		// when
		Optional<PatientProjection> patientName = patientRepository.findPatientUsingProjectionById(patient.getId());

		// then
		String expectedFullName = patient.getFirstName().concat(" ").concat(patient.getLastName());
		String expectedFullNameWithSalutation = new StringBuilder(
				patient.getGender().equals(Gender.MALE) ? "Mr" : "Mrs").append(" ").append(expectedFullName).toString();
		Integer expectedAge = utils.getAgeFromDob(patient.getDob());
		String expectedGenderString = patient.getGender().toString();
		assertThat(patientName.isPresent(), is(true));
		assertThat(patientName.get().getFullName(), is(equalTo(expectedFullName)));
		assertThat(patientName.get().getFullNameWithSalutation(), is(equalTo(expectedFullNameWithSalutation)));
		assertThat(patientName.get().getAge(), is(equalTo(expectedAge)));
		assertThat(patientName.get().getGenderAsAString(), is(equalTo(expectedGenderString)));
	}
	
	/**
	 * Projections: DTO based open projection.
	 */
	@Test
	public void testGetPatientNameDtoById() {
		// given
		Patient patient = createTestPatient();
		patientRepository.save(patient);
		
		// when
		Optional<PatientNameDto> patientName = patientRepository.getPatientNameDtoById(patient.getId());
		
		// then
		assertThat(patientName.isPresent(), is(true));
		assertThat(patientName.get().getFirstName(), is(equalTo(patient.getFirstName())));
		assertThat(patientName.get().getLastName(), is(equalTo(patient.getLastName())));
	}

	@After
	@Before
	public void beforeAndAfterTest() {
		clearDataStore();
	}
	
	private void clearDataStore() {
		patientVitalRepository.deleteAllInBatch();
		patientRepository.deleteAllInBatch();
	}
	
	public static Patient createTestPatient() {
		Patient patient = new Patient();
		patient.setFirstName("Bob");
		patient.setLastName("Sargent");
		Calendar dob = Calendar.getInstance();
		dob.add(Calendar.YEAR, -20);
		patient.setDob(dob);
		patient.setSsn(UUID.randomUUID().toString());
		patient.setGender(Gender.values()[ThreadLocalRandom.current().nextInt(0, Gender.values().length -1)]);

		List<String> bloodGroups = Arrays.asList("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+");
		patient.setBloodGroup(bloodGroups.get(ThreadLocalRandom.current().nextInt(0, 8)));
		return patient;
	}
}
