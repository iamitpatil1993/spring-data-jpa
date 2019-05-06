/**
 * 
 */
package com.example.persistence.dao.auto;

import java.util.Calendar;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.persistence.dao.CustomPatientRepository;
import com.example.persistence.model.Patient;
import com.example.persistence.model.PatientVital;

/**
 * Defines patient repository, which extends one of spring repository, in order
 * to leverage spring-data-jpa auto-generated implementation.
 * 
 * @author amit
 *
 */
public interface PatientRepository extends JpaRepository<Patient, Integer>, CustomPatientRepository { // extend custom repo. interface

	// find, read and get are synonyms
	public Patient findPatientBySsn(final String ssn); // find verb, Patient object

	public Patient readAnyPatienttBySsnIgnoreCase(final String ssnSearchString); // read verb, AnyPatient object (Object
																					// can be anything as you want)

	public Patient getByFirstNameOrLastName(final String firstName, final String lastName); // get verb, No Object
																							// (object is not required,
																							// unless Case of
																							// 'Distinct)'

	public Patient getByFirstNameAndLastName(final String firstName, final String lastName);

	public Patient getByFirstNameIgnoreCaseAndLastName(final String firstName, final String lastName); // Ignore case
																										// firstName
																										// only

	public Patient findByFirstNameAndLastNameAllIgnoringCase(final String firstName, final String lastName); // Ignore
																												// case
																												// for
																												// all,
																												// synonym
																												// for
																												// this
																												// is
																												// findByFirstNameAndLastNameAllIgnoreCase

	public Patient findDistinctPatientByFirstName(final String firstName);

	public List<Patient> findByDob(final Calendar dob); // with date exact match

	public List<Patient> findByDobIsAfter(final Calendar dob); // with date after

	public List<Patient> findByDobIsBetween(final Calendar equalToOrAfterDate, final Calendar beforeOrEqualDate); // with
																													// date
																													// between

	public int countBySsnIsNull(); // count verb, IsNull condition

	public List<Patient> findPatientsByBloodGroupIsIn(final List<String> bloodGroups); // count verb, IsNull condition

	public List<Patient> findPatientsByFirstNameStartingWithOrLastNameStartingWithOrderByFirstNameAscLastNameAsc(
			final String fistNameSearchString, final String lastNameSearchString); // parameter names does not matter,
																					// rather their position is used.,
																					// with Sorting

	/**
	 * by default this is JPQL, we can change it to native, sql by enabling
	 * 'nativeQuery' property
	 */
	@Query(value = "SELECT p FROM Patient p WHERE p.ssn IS NULL")
	public List<Patient> findPatientWithNullSsn();

	/**
	 * we can select individual columns as well, if we do not want to load entire
	 * entity state with single-value (eager) associations
	 */
	@Query(value = "SELECT p.id FROM Patient p WHERE p.ssn IS NULL")
	public List<Integer> findAllPatientIdsWithNullSsn();

	/**
	 * we can perform any query, with joins to other entities as well.
	 */
	@Query(value = "SELECT p FROM Patient p LEFT JOIN p.vitals v WHERE v.id IS NULL GROUP BY p")
	public List<Patient> findAllPatientsWithoutAnyVital();

	/**
	 * we can use native queries as well. we need to use select in order to select
	 * entity type as a return type
	 */
	@Query(value = "SELECT * FROM patient p WHERE p.is_deleted = false", nativeQuery = true)
	public List<Patient> findAllPatientsWithIsDeletedFalse();

	/**
	 * we can refer JPQL queries defined at Entity level as well. we can select
	 * completely different entity type as well. (It's no tgood practice though, to
	 * have this query here)
	 */
	@Query(name = "PatientVital.findAll") //
	public List<PatientVital> findPatientVitals();

	/**
	 *
	 * positional parameters are matched to methods parameters using order ( and not
	 * name, so name can be anything) exactly same way we can pass parameters to
	 * Native query as well
	 */
	@Query(value = "SELECT p FROM Patient p WHERE p.firstName = ?1 OR p.lastName = ?2")
	public List<Patient> findAllWithFirstNameOrLastNameUsingPositionalParameters(final String fName, final String lName,
			Sort orderBy);

	/**
	 * Sort argument does not work for NamedQueries, because named queries get
	 * compiled at load time, and can't be altered dynamically. Sort/Pagination can
	 * not be used with Native queries. Named parameters are matched based on @Param
	 * annotation exactly same way we can pass parameters to Native query as well,
	 * Sort argument does not work for NamedQueries, becaue named queries get
	 * compiled at load time, and can't be altered dynamically.
	 */
	@Query(value = "SELECT p FROM Patient p WHERE p.firstName = :firstName OR p.lastName = :lastName") //
	public List<Patient> findAllWithFirstNameOrLastNameUsingNamedParameters(@Param("firstName") final String fName,
			@Param("lastName") final String lName, Sort orderBy);

	/**
	 * Method can have return type of Slice, Page or List when pageable is used.
	 * method can not have pageable AND sort both argument. We need to use sorting
	 * functionality from pageable
	 */
	@Query(value = "SELECT p FROM Patient p")
	public List<Patient> findAllPatients(final Pageable pageable);

	/**
	 * we can do pagination on native query as well in same way as JPQL
	 */
	@Query(value = "SELECT * FROM patient", nativeQuery = true)
	public Page<Patient> findAllPatientsUsingNativeQueryAndPagination(Pageable pageable);

	/**
	 * Find all patients by blood group. Collection of bloodGroup used for IN
	 * clause. Same way NOT IN can be done. NOTE: check how named parameter defined
	 * in query, no curly braces used as a typical IN clause.
	 */
	@Query(value = "SELECT p FROM Patient p WHERE p.bloodGroup IN :bloodGroups")
	public List<Patient> findAllByBloodGroup(@Param("bloodGroups") List<String> bloodGroups);

	/**
	 * We can perform DML operations like INSERT, UPDATE, DELETE, and can perform
	 * DDL using @Modifying NOTE: this query must be executed in transactional
	 * context there is no transaction context by default.
	 */
	@Modifying // Indicates a query method should be considered as modifying query
	@Query(value = "UPDATE Patient p SET p.ssn = :ssn WHERE p.id = :patientId")
	public int updateSsnByPatientId(@Param("patientId") final Integer patientId, @Param("ssn") final String ssn);
}
