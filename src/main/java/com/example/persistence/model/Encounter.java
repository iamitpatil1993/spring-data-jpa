/**
 * 
 */
package com.example.persistence.model;

import java.util.Calendar;

/**
 * @author amit
 *
 */
public class Encounter {

	private String encounterId;

	private Integer patientId;

	private Calendar createdTimestamp;

	private Calendar updatedTimestap;

	public String getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(String encounterId) {
		this.encounterId = encounterId;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public Calendar getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Calendar createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public Calendar getUpdatedTimestap() {
		return updatedTimestap;
	}

	public void setUpdatedTimestap(Calendar updatedTimestap) {
		this.updatedTimestap = updatedTimestap;
	}


}
