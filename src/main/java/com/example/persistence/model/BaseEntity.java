package com.example.persistence.model;

import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Base entity for all entities to define common state in all entities.
 * 
 * @author amit
 *
 */

@MappedSuperclass
public class BaseEntity {

	@Column(name = "id") // physical annotations should be first. (For readability)
	@Basic
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "created_date")
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	@Basic
	private Calendar createdDate;

	@Column(name = "updated_date")
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	@Basic
	private Calendar updatedDate;

	@Column(name = "is_deleted")
	@Basic
	private boolean isDeleted = false;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public Calendar getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Calendar updatedDate) {
		this.updatedDate = updatedDate;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseEntity other = (BaseEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
