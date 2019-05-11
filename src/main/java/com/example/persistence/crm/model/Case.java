/**
 * 
 */
package com.example.persistence.crm.model;

import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.example.persistence.model.BaseEntity;

/**
 * @author amit
 *
 */

@Entity
@Table(name = "sf_case")
public class Case extends BaseEntity {

	@Basic
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private CaseStatus caseStatus;

	@Basic
	@Column(name = "owner_id", nullable = false)
	private String ownerId;

	@Basic
	@Column(name = "description")
	private String description;

	@Basic
	@Column(name = "closed_on")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar closedOn;

	public CaseStatus getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(CaseStatus caseStatus) {
		this.caseStatus = caseStatus;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
