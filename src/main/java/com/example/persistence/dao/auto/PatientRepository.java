/**
 * 
 */
package com.example.persistence.dao.auto;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

import com.example.persistence.dao.AnotherCustomPatientRepository;
import com.example.persistence.dao.BaseEntityRepository;
import com.example.persistence.dao.CustomPatientRepository;
import com.example.persistence.dao.projections.PatientNameProjection;
import com.example.persistence.dao.projections.PatientProjection;
import com.example.persistence.dto.PatientDto;
import com.example.persistence.dto.PatientNameDto;
import com.example.persistence.model.Patient;
import com.example.persistence.model.PatientVital;
import com.example.persistence.model.VitalType;

/**
 * Defines patient repository, which extends one of spring repository, in order
 * to leverage spring-data-jpa auto-generated implementation.
 * 
 * @author amit
 *
 */
public interface PatientRepository extends BaseEntityRepository<Patient, Integer>, CustomPatientRepository, AnotherCustomPatientRepository, QuerydslPredicateExecutor<Patient> { // extend custom repo. interface

	// find, read and get are synonyms
	@Nullable
	public Patient findPatientBySsn(final String ssn); // find verb, Patient object

	@Nullable
	public Optional<Patient> readAnyPatienttBySsnIgnoreCase(final String ssnSearchString); // read verb, AnyPatient object (Object
																					// can be anything as you want)
	@Nullable
	public Optional<Patient> getByFirstNameOrLastName(final String firstName, final String lastName); // get verb, No Object
																							// (object is not required,
																							// unless Case of
																							// 'Distinct)'
	@Nullable
	public Patient getByFirstNameAndLastName(final String firstName, final String lastName);

	@Nullable
	public Patient getByFirstNameIgnoreCaseAndLastName(final String firstName, final String lastName); // Ignore case
																										// firstName
																										// only
	@Nullable
	public Patient findByFirstNameAndLastNameAllIgnoringCase(final String firstName, final String lastName); // Ignore
																												// case
																												// for
																												// all,
																												// synonym
																												// for
																												// this
																												// is
																												// findByFirstNameAndLastNameAllIgnoreCase
	@Nullable
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
	
	@Transactional // since these methods performing DML, must be transactional
	public void deleteById(final Integer patientId); // delete verb can be used to delete records
	
	@Transactional // since these methods performing DML, must be transactional
	public void removePatientByBloodGroup(final String bloodGroup); // remove (synonym to delete)  verb can be used to delete records

	/**
	 * Since NonNullAPi is applied at package level, by default arguemenets passed,
	 * return values returned must not be null.
	 * 
	 * So, if null is passed here as a argument, by default Spring will throw IllegalArgumentException.
	 * If method returns null, spring will throw exception.
	 * 
	 * If we want to change this default behavior which is applied to entire package due to @NonNullApi at package level,
	 * we can add @Nullable annotation at method argument level which can be null.
	 * 
	 * If we want to allow null value to be returned from method, we can annoate the method with @Nullable.
	 */
	public Patient getBySsn(final String ssn);
	
	public @Nullable Patient readBySsn(@Nullable final String ssn);
	
	/**
	 * No need of @Nullable here, it will return empty optional if no records found,
	 * instead of throwing EmptyResultDataAccessException.
	 * 
	 * Spring handles Optional in result value present and absent cases as expected.
	 */
	public Optional<Patient> findBySsn(@Nullable final String ssn);
	
	/**
	 * Refers nested properties via associations. We can refer properties of associate entities as well.
	 * We need to follow some convention to declare predicate conditions.
	 * 
	 * We can use underscore as well to manually define traversal points.
	 * Refer docs https://docs.spring.io/spring-data/jpa/docs/2.1.8.RELEASE/reference/html/#repositories.query-methods.query-property-expressions
	 * 
	 * @param vitalType property from associated PatinetVital entity
	 * @param value property from associated PatinetVital entity
	 */
	public List<Patient> findDistinctPatientByVitalsVitalAndVitalsValueIsGreaterThan(final VitalType vitalType, final Double value); 
	//public List<Patient> findDistinctPatientByVitals_VitalAndVitals_ValueIsGreaterThan(final VitalType vitalType, final Double value); // same as above, only uses underscore  to manually define traversal points 

	/**
	 * We can specify records to limit using 'top' or 'first' keyword before 'By'.
	 * 
	 * if we do not specify number, defaults to one
	 * 
	 * @param type
	 * @param value
	 * @return Only one record
	 */
	public List<Patient> findTopPatientByVitalsVitalAndVitalsValueIsGreaterThanOrderByVitalsValueDesc(
			final VitalType type, final double value);
	
	// public List<Patient> findTop1PatientByVitalsVitalAndVitalsValueIsGreaterThanOrderBVitalsValueDesc(final VitalType type, final double value); // is same as above

	public Optional<Patient> findTopByVitalsVitalAndVitalsValueIsGreaterThanOrderByVitalsValueDesc(final VitalType type,
			final double value);

	/**
	 * We can get top/first n results from paginated result as well. In that case,
	 * it will return top n records from within page.
	 */
	public Page<Patient> findTop3ByVitalsVitalAndVitalsValueIsGreaterThanOrderByVitalsValueDesc(final VitalType type,
			final double value, Pageable pageable);

	@Query("SELECT p FROM Patient p")
	public Stream<Patient> findAllViaStream(); // we can read data as a stream instead
	
	/**
	 * Exactly similar to normal async methods, Query methods can be async and can return Future and ListenableFuture or void
	 * @param pageable
	 * @return ListenableFuture on which we can attach callback handler
	 */
	@Async(value = "threadPoolTaskExecutor")
	@Query(value = "SELECT p FROM Patient p")
	ListenableFuture<List<Patient>> findAllPatientss(Pageable pageable); 

	/**
	 * Spring detects JPA named query based on name of method. Named query name should follow convenstion as 
	 * Model.methodName
	 * here model is Patient and method name is findAllByBloodGroup so it matches to named query 'Patient.findAllByBloodGroup'
	 * refer https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.named-queries
	 * @param bloodGroup 	
	 * @return
	 */
	List<Patient> findAllByBloodGroup(@Param("bloodGroup") final String bloodGroup);
	
	/**
	 * This query definition will override query defined at Model {@link Patient}.
	 * So, Query defined at Query method always overrides the matching Named query.
	 * 
	 * We can use SpEL to define entity type in generic way, spring will determine entity type based on entity attached to this repository. Which is Patient.
	 * Spring will substitute this SpEl with entity name before passing JPQL for execution.
	 * 
	 * This helps us to, change entity name without affecting query by using @Entity(name = "EntityName")
	 * return Patients and PatientVital with isDeleted = true 
	 * 
	 */
	@Query(value = "SELECT p FROM #{#entityName} p JOIN p.vitals v WHERE ((v.vital = 'SYS_BP' AND v.value > 140) OR (v.vital = 'DI_BP' AND v.value > 90))")
	List<Patient> findAllHypertensionPatients();
	
	@QueryHints(value = {@QueryHint(name = "org.hibernate.flushMode", value = "COMMIT")})
	@Modifying(flushAutomatically = false) 
	@Query(value = "UPDATE Patient p SET p.firstName = ?1, p.lastName = ?2 WHERE p.id = ?3")
	public int updateNameWithoutFlushingExistinChanges(final String fName, final String lName, final Integer id);
	
	@QueryHints(value = {@QueryHint(name = "org.hibernate.flushMode", value = "COMMIT")})
	@Modifying(flushAutomatically = true) 
	@Query(value = "UPDATE Patient p SET p.firstName = ?1, p.lastName = ?2 WHERE p.id = ?3")
	public int updateNameWithFlushingExistinChanges(final String fName, final String lName, final Integer id);
	
	/**
	 * Just use return type of Projection interface, and spring will handle
	 * everything Spring will create optimized query (select clause) to fetch only
	 * properties defined in Projection interface.
	 * 
	 * If we check query executed, spring will not select entire entity, rather
	 * select clause will only contain properties defined in PatientNameProjection
	 * 
	 * @return Subset of patient attributes via Projection interface proxy.
	 */
	public Optional<PatientNameProjection> findPatientNamesById(final Integer id);
	
	public Optional<PatientProjection> findPatientUsingProjectionById(final Integer id);
	
	public Optional<PatientNameDto> getPatientNameDtoById(final Integer id);
	
	/**
	 * Even though query methods and repository implementations are by default transactional, we can use this annotation to override 
	 * default transaction configuration.
	 */
	@Transactional(readOnly = true, timeout = 5) 
	public Optional<PatientDto> getPatientDtoById(final Integer id);
	
	/**
	 * If we want to override default transaction configuration in repository
	 * implementation, (like SimpleJpaRepository) we can override, redeclare
	 * repository method in our repository interface with @Transactional annotation
	 * to customize the transactional configuration.
	 * 
	 * This method overrides transaction configuration of
	 * {@link CrudRepository#findById(Object)}, setting smaller timeout value.
	 * 
	 * NOTE: Ideally we should not define transactional configuration on repository
	 * method, rather service classes who calls repositories should define
	 * transaction demarcation and configuration. And we should stick to default
	 * transaction configuration provide by spring on query methods and repository
	 * methods.
	 */
	@Override
	@Transactional(readOnly = true, timeout = 3)
	Optional<Patient> findById(Integer id);
}
