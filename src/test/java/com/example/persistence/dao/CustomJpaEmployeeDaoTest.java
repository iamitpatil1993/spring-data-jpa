/**
 * 
 */
package com.example.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.example.persistence.configuration.AppConfiguration;
import com.example.persistence.model.Employee;

/**
 * @author amit
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class CustomJpaEmployeeDaoTest {

	@Autowired
	@JPA
	@Custom
	private EmployeeRepository employeeRepository;

	/**
	 * Test method for
	 * {@link com.example.persistence.dao.CustomJpaEmployeeDao#findById(java.lang.String)}.
	 */
	@Test
	public void testFindById() {
		// Given
		Employee employeeWithPk = createDummyEmployee();

		// when
		Employee result = employeeRepository.findById(employeeWithPk.getId());

		// then
		assertNotNull(result);
		assertEquals(employeeWithPk.getId(), result.getId());
	}

	/**
	 * Test method for
	 * {@link com.example.persistence.dao.CustomJpaEmployeeDao#add(com.example.persistence.model.Employee)}.
	 */
	@Test
	public void testAdd() {
		// when
		Employee employeeWithPk = createDummyEmployee();

		// then
		assertNotNull(employeeWithPk);
		assertNotNull(employeeWithPk.getId());
		assertNotNull(employeeRepository.findById(employeeWithPk.getId()));
	}

	@Test
	public void testDelete() {
		// given
		Employee employeeWithPk = createDummyEmployee();

		// when
		employeeRepository.delete(employeeWithPk.getId());

		// then
		assertNull(employeeRepository.findById(employeeWithPk.getId()));

	}

	private Employee createDummyEmployee() {
		Employee employee = new Employee();
		employee.setFirstName("Bob");
		employee.setLastName("Sargent");
		Calendar dob = Calendar.getInstance();
		dob.add(Calendar.YEAR, 20);
		employee.setDob(dob);

		Employee employeeWithPk = employeeRepository.add(employee);
		return employeeWithPk;
	}
}
