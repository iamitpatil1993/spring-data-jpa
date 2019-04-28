/**
 * 
 */
package com.example.persistence.model;

import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author amit
 *
 */
@Entity
public class Patient extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -73106198610868767L;

	@Basic
	@Column(name = "first_name")
	private String firstName;

	@Basic
	@Column(name = "last_name")
	private String lastName;

	@Basic
	@Column(name = "dob")
	private Calendar dob;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Calendar getDob() {
		return dob;
	}

	public void setDob(Calendar dob) {
		this.dob = dob;
	}

}
