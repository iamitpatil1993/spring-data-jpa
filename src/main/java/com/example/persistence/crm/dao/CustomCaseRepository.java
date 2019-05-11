/**
 * 
 */
package com.example.persistence.crm.dao;

import java.util.List;

import com.example.persistence.crm.model.Case;

/**
 * @author amit
 *
 */
public interface CustomCaseRepository {
	
	List<Case> findAllInProgressCaseCreatedInLastMonth();

}
