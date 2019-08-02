/**
 * 
 */
package com.example.persistence.model;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

/**
 * @author amit
 *
 */
@NamedQueries({
	@NamedQuery(name = "Patient.findAllByBloodGroup", query = "SELECT p FROM Patient p WHERE isDeleted = false AND p.bloodGroup = :bloodGroup"), 
	@NamedQuery(name = "Patient.findAllHypertensionPatients", query = "SELECT p FROM Patient p JOIN p.vitals v WHERE p.isDeleted = false AND v.isDeleted = false AND ((v.vital = 'SYS_BP' AND v.value > 140) OR (v.vital = 'DI_BP' AND v.value > 90))")
})
@Entity
@Table(name = "patient")
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
	@Temporal(TemporalType.DATE)
	private Calendar dob;

	@Basic
	@Column(name = "ssn", unique = true)
	private String ssn;

	@Basic
	@Column(name = "blood_group")
	private String bloodGroup;

	@OneToMany(mappedBy = "patient")
	@Getter
	@Setter
	private Set<PatientVital> vitals = new HashSet<>();
	
	@Basic
	@Enumerated(EnumType.STRING)
	@Column(name = "gender")
	private Gender gender;

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

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

}
