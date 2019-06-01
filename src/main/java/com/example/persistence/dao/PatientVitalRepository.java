/**
 * 
 */
package com.example.persistence.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.persistence.model.PatientVital;

/**
 * @author amit
 *
 */
// Custom Repository implementations can be share among different repositories
@Lazy(value = true) // Individual repository can also be marked to get initialized lazily, if we do
					// not want to enable lazy initialization for all repositories at configuration
					// level..
public interface PatientVitalRepository extends JpaRepository<PatientVital, Integer>, AnotherCustomPatientRepository, CustomPatientVitalRepository {

	/**
	 * We can not use implicit or explicit joins in bulk delete or update operations, HQL does not allows this,
	 * so we need to use sub-select (inner query)
	 * Refer https://stackoverflow.com/questions/40755008/spring-data-rest-update-produce-cross-join-sql-error 
	 * @param bloodGroup
	 */
	@Modifying // need to use since operations is DML
	@Transactional // need to be transactional
	@Query(value = "DELETE FROM PatientVital pv WHERE pv.patient IN (SELECT p FROM Patient p WHERE p.bloodGroup = :bloodGroup)")
	public void deleteAllByPatientsWithBloodGroup(@Param("bloodGroup") final String bloodGroup);
	
	@Query(value = "SELECT pv FROM PatientVital pv")
	public List<PatientVital> findAllPatientVitals(Sort sort);
	
	/**
	 * We can use @EntityGraph on query method to provide EntityGraph to be used to
	 * fetch entity. type attribute is used to specify mode of EntityGraph Fetch
	 * 
	 * @return PatientVitals with Patient association loaded eagerly.
	 */
	@EntityGraph(value = "PatientVital.graph1", type = EntityGraphType.FETCH)
	public List<PatientVital> getPatientVitalByIsDeleted(boolean isDeleted);
	
	/**
	 * This is how we can define AD-HOC entity graph definition on an repository
	 * query method. NOTE: I THINK, we can define entity graph at root entity level
	 * only and can not define subgraphs using this approach, spring document also
	 * does not say anything about AD-HOC subgraphs definitions
	 */
	@EntityGraph(attributePaths = { "vital", "value", "patient" }, type = EntityGraphType.FETCH)
	public Optional<PatientVital> getPatientVitalWithPatientById(final Integer id);
}
