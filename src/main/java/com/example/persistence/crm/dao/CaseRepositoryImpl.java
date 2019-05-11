/**
 * 
 */
package com.example.persistence.crm.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.example.persistence.crm.model.Case;
import com.example.persistence.crm.model.CaseStatus;

/**
 * This class demonstrates custom repository implementation of entity belongs to
 * another PU (my-pu-2)
 * 
 * @author amit
 *
 */
public class CaseRepositoryImpl implements CustomCaseRepository {

	// we must provide persistence unit to use here, be cause there are two
	// EntityManagerFactory classes in spring application context one for each PU.
	// so we need to tell spring which PU to use to get EntityManager.
	@PersistenceContext(unitName = "my-pu-2")
	private EntityManager em;

	// no need to provide @Transactional here, repository classes are already
	// transactional via default implementation provided by spring
	@Transactional(transactionManager = "jpaPlatformEntityManager")
	@Override
	public List<Case> findAllInProgressCaseCreatedInLastMonth() {

		Calendar fromDate = Calendar.getInstance();
		fromDate.add(Calendar.MONTH, -1);

		Calendar toDate = Calendar.getInstance();
		return em.createQuery(
				"SELECT c FROM Case c WHERE c.caseStatus = :caseStatus AND c.isDeleted = false AND c.createdDate BETWEEN :fromDate ANd :toDate",
				Case.class).setParameter("caseStatus", CaseStatus.IN_PROGRESS).setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).getResultList();
	}

}
