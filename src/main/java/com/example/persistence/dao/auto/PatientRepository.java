/**
 * 
 */
package com.example.persistence.dao.auto;

import java.util.Calendar;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.persistence.model.Patient;

/**
 * Defines patient repository, which extends one of spring repository, in order
 * to leverage spring-data-jpa auto-generated implementation.
 * 
 * @author amit
 *
 */
public interface PatientRepository extends JpaRepository<Patient, Integer> {

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
}
