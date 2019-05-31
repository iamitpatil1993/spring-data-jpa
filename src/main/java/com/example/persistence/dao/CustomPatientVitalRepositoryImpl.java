/**
 * 
 */
package com.example.persistence.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.persistence.model.PatientVital;

/**
 * @author amit
 *
 */
@Repository
public class CustomPatientVitalRepositoryImpl implements CustomPatientVitalRepository, InitializingBean {
	
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Object graph will load patient entity (in single value association) despite it is marked to be loaded lazily
	 */
	@Transactional(readOnly = true)
	@Override
	public Optional<PatientVital> findByIdWithPatient(Integer id) {
		
		EntityGraph graph1 = em.getEntityGraph("PatientVital.graph1");
		Map<String, Object> queryHints = new HashMap<>(1);
		queryHints.put("javax.persistence.fetchgraph", graph1);
		
		return Optional.ofNullable(em.find(PatientVital.class, id, queryHints));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		assert em != null :  "Required Entity manager injected null";
	}

}
