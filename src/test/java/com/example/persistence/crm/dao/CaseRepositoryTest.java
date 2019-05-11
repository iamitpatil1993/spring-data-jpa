/**
 * 
 */
package com.example.persistence.crm.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.runner.RunWith;

import com.example.persistence.BaseTest;
import com.example.persistence.crm.model.Case;
import com.example.persistence.crm.model.CaseStatus;
import static org.hamcrest.Matchers.*;

/**
 * @author amit
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class CaseRepositoryTest extends BaseTest {

	// Spring JpaRepository for Case entity which belongs to another PU, and uses
	// it's
	// own EntitymanagerFactory, transaction manager.
	@Autowired
	private CaseRepository caseRepository;

	@Test
	public void test() {
		assertNotNull(caseRepository);
	}

	/**
	 * Test spring generated JpaRepository
	 */
	@Test
	public void testSave() {
		// given
		Case case1 = createTestCase();

		// when
		caseRepository.save(case1);

		// then
		assertNotNull(case1.getId());
	}

	/**
	 * Test custom implementation 
	 */
	@Test
	public void testFindAllInProgressCaseCreatedInLastMonth() {
		// given
		Case case1 = createTestCase();
		caseRepository.save(case1);

		// when
		List<Case> inProgressCasesCreatedInLastMonth = caseRepository.findAllInProgressCaseCreatedInLastMonth();

		// then
		assertThat(inProgressCasesCreatedInLastMonth, is(not(empty())));
	}

	private Case createTestCase() {
		Case case1 = new Case();
		case1.setCaseStatus(CaseStatus.IN_PROGRESS);
		case1.setDescription("Gateway timeout error on adding item to cart");
		case1.setOwnerId(UUID.randomUUID().toString());
		return case1;
	}

}
