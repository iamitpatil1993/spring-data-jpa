/**
 * 
 */
package com.example.persistence.dao;

import com.example.persistence.model.Employee;

/**
 * @author amit
 *
 */
public interface EmployeeRepository {
	
	public Employee findById(final Integer id);
	
	public Employee add(Employee employee);
	
	public void delete(final Integer id);

}
