/**
 * 
 */
package com.example.persistence.dao;

import java.util.List;

import com.example.persistence.model.Patient;

/**
 * Another Custom Patient repository that will add to base spring data jpa PatientRepository.
 * These custom patient repository implementations can be shared among any base repositories.
 * 
 * @author amit
 *
 */
public interface AnotherCustomPatientRepository {

	public List<Patient> doSomething();
	
}
