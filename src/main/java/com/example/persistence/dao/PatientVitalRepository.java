/**
 * 
 */
package com.example.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.persistence.model.PatientVital;

/**
 * @author amit
 *
 */
public interface PatientVitalRepository extends JpaRepository<PatientVital, Integer> {

}
