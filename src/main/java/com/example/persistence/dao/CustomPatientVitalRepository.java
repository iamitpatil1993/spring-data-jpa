/**
 * 
 */
package com.example.persistence.dao;

import java.util.Optional;

import com.example.persistence.model.PatientVital;

/**
 * @author amit
 *
 */
public interface CustomPatientVitalRepository {
	
	public Optional<PatientVital> findByIdWithPatient(final Integer id);
	
	public Optional<PatientVital> findByIdWithPatientUsingJPQL(final Integer id);
	
	public Optional<PatientVital> findByIdWithoutPatientUsingJPQL(final Integer id);

}
