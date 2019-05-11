/**
 * 
 */
package com.example.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.persistence.model.Employee;

/**
 * 
 * Provides custom implementation of EmployeeDao using EntityManager. It does
 * not uses spring-data-jpa features and auto-generated DAOs. Also it does not
 * considers transactional aspects for now, transactions will be covered in
 * later tutorials.
 * 
 * @author amit
 *
 */
@Repository
@JPA
@Custom
public class CustomJpaEmployeeDao implements EmployeeRepository, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomJpaEmployeeDao.class);

	// we need to specify which EntityManagerFactory to use.
	@PersistenceContext(unitName = "foo-unit")
	private EntityManager em;

	@Override
	public void afterPropertiesSet() throws Exception {
		assert em != null : "EntityManager can not be null, and must be injected in order to CustomJpaEmployeeDao";
	}

	// since this is custom implementation class, we need to specify
	// transactionManager to use, since there are two in application
	// We do not need to specify this in case of repository classes because in case of repositories, spring 
	// knows which entity manager to use via @EnableJpaRepositoties annotation attributes.
	@Transactional(transactionManager = "transactionManager")
	@Override
	public Employee findById(Integer id) {
		return em.find(Employee.class, id);
	}

	/**
	 * Need to declare transaction since, JPA operations (DML) can not be executed
	 * without transaction. This method will join existing transaction if exists.
	 * (if called from other repository method or service method at upper level.
	 * 
	 * Ideally transaction management should be at service layer, for now we are
	 * just using repositories. And will use service classes and transaction in
	 * further tutorials.
	 */
	@Transactional(transactionManager = "transactionManager") 
	@Override
	public Employee add(Employee employee) {
		em.persist(employee);
		LOGGER.info("Employee created with employeeId :: {}", employee.getId());
		return employee;
	}

	@Transactional(transactionManager = "transactionManager")
	@Override
	public void delete(Integer id) {
		Employee employee = findById(id);
		if (employee != null) {
			em.remove(employee);
			LOGGER.info("Employee removed with employeeId :: {}", employee.getId());
		}
	}
}
