/**
 * 
 */
package com.example.persistence.dao.auto;

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
	// nothing to do here for now as long as custom implementation not required and
	// spring JpaRepository implementation is sufficient.
}
