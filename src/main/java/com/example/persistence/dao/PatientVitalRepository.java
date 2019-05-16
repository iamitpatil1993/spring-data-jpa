/**
 * 
 */
package com.example.persistence.dao;

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
public interface PatientVitalRepository extends JpaRepository<PatientVital, Integer> {

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
}
