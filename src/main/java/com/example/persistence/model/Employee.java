package com.example.persistence.model;

import javax.persistence.*;
import java.util.Calendar;

/**
 * @author amit
 *
 */

@Entity
@Table(name = "employee")
public class Employee extends BaseEntity {

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "dob")
	@Temporal(TemporalType.DATE)
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
