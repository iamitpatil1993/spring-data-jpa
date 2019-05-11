/**
 * 
 */
package com.example.persistence.crm.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.persistence.crm.model.Case;

/**
 * @author amit
 *
 */
public interface CaseRepository extends JpaRepository<Case, Integer>, CustomCaseRepository {
	// Nothing to do here for now.
}
