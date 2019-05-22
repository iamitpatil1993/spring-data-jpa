/**
 * 
 */
package com.example.persistence.dao;

import java.util.List;

import com.example.persistence.model.Patient;

/**
 * Another Custom Patient repository that will add to base spring data jpa PatientRepository.
 * @author amit
 *
 */
public interface AnotherCustomPatientRepository {

	public List<Patient> doSomething();
	
}
