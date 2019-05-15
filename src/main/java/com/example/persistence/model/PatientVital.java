/**
 * 
 */
package com.example.persistence.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * @author amit
 *
 */

@NamedQueries(value = {
		@NamedQuery(query = "SELECT pv FROM PatientVital pv", name = "PatientVital.findAll")
})
@Entity
@Table(name = "patient_vital")
public class PatientVital extends BaseEntity {

	@Basic
	@Column(name = "vital", nullable = false)
	@Enumerated(EnumType.STRING)
	private VitalType vital;

	@Basic
	@Column(name = "value", nullable = false, precision = 2)
	private Double value;

	@ManyToOne(optional = false)
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;
	
	@Basic
	@Column(name = "encounterId")
	private String encounterId;

	public VitalType getVital() {
		return vital;
	}

	public void setVital(VitalType vital) {
		this.vital = vital;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(String encounterId) {
		this.encounterId = encounterId;
	}
}
