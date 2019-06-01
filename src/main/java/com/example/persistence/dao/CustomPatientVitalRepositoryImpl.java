/**
 * 
 */
package com.example.persistence.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
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

	/**
	 * Using Entity graph with Named Query to fetch Patient single value association
	 * eagerly. We need to use javax.persistence.fetchgraph or
	 * javax.persistence.loadgraph query hint/
	 */
	@Override
	public Optional<PatientVital> findByIdWithPatientUsingJPQL(Integer id) {
		try {
			return Optional.ofNullable(em.createNamedQuery("PatientVital.findById", PatientVital.class)
					.setHint("javax.persistence.fetchgraph", em.getEntityGraph("PatientVital.graph1"))
					.setParameter("id", id).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		} catch (NonUniqueResultException e) {
			throw e;
		}
	}

	/**
	 * Execute JPQL wiithout EntityGraph (default graph actually). It will not load
	 * Patient single value association.
	 */
	@Override
	public Optional<PatientVital> findByIdWithoutPatientUsingJPQL(Integer id) {
		try {
			return Optional.ofNullable(em.createNamedQuery("PatientVital.findById", PatientVital.class)
					.setParameter("id", id).getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		} catch (NonUniqueResultException e) {
			throw e;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		assert em != null :  "Required Entity manager injected null";
	}
}
