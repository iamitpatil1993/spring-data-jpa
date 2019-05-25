/**
 * 
 */
package com.example.persistence.dao;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.persistence.model.Patient;

/**
 * This is the interface that, out custom repository will implement.
 * This interface defines contract for our custom patient repository.

 * @author amit
 *
 */
public interface CustomPatientRepository {

	/**
	 * Custom patient repository method.
	 * @return All diabetic patients
	 */
	public List<Patient> findAllDiabeticPatients();
	
	public Optional<Patient> findByPatientIdUsingQueryDsl(final Integer patientId);
	
	public List<Patient> getPatientsCreatedInLastMonth();
	
	public Optional<Calendar> findYungestPatient();
	
	public Map<String, Calendar> findYungestPatientGroupByBloodGroup();
	
}
