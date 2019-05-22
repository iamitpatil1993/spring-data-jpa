/**
 * 
 */
package com.example.persistence.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;

import com.example.persistence.model.Patient;

/**
 * We can have multiple custom implementations of repository and we can add
 * these all custom functionalities to spring data jpa base repository by
 * extending this interface (custom interfaces)
 * 
 * @author amit
 *
 */
// Name of this class need not be BaseRepositoryInterface + Impl (PatientRepositoryImpl), it can be custom repository interface + Impl.
public class AnotherCustomPatientRepositoryImpl implements AnotherCustomPatientRepository, InitializingBean {

	@PersistenceContext
	private EntityManager em;

	@Transactional(readOnly = true)
	@Override
	public List<Patient> doSomething() {
		return em.createQuery("SELECT p FROM Patient p ORDER BY updatedDate desc", Patient.class).setMaxResults(10)
				.getResultList();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		assert em != null : "Entity manager injected null";
	}
}
