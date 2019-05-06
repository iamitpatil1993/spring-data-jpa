/**
 * 
 */
package com.example.persistence.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.persistence.model.Patient;
import com.example.persistence.model.VitalType;

/**
 * This is custom Patient repository, which adds custom functionality to
 * spring-data-jpa auto generated JpaRepository (PatientRepository)
 * 
 * We need to create custom interface (here CustomPatientRepository) in order define custom repository.
 * @author amit
 *
 */
@Repository
public class PatientRepositoryImpl implements CustomPatientRepository, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(PatientRepositoryImpl.class);
	private EntityManager em;

	@Autowired
	public PatientRepositoryImpl(EntityManager em) {
		this.em = em;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.example.persistence.dao.CustomPatientRepository#findAllDiabeticPatients()
	 */
	@Transactional(readOnly = true)
	@Override
	public List<Patient> findAllDiabeticPatients() {
		String query = "SELECT p FROM Patient p WHERE p.id IN (SELECT pv.patient.id FROM PatientVital pv WHERE isDeleted = false AND vital = :vitalType AND value >= 150)";
		return em.createQuery(query, Patient.class).setParameter("vitalType", VitalType.SUGAR).getResultList();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		assert em != null : "Entity manager required to be injected to use repository";
	}

}
