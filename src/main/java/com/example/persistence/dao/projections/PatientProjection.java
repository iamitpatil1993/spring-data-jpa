package com.example.persistence.dao.projections;

import org.springframework.beans.factory.annotation.Value;

import com.example.persistence.model.Gender;

/**
 * define patient projection, that adds custom/new properties from existing
 * patient attributes using
 * <li>SpEl</li>
 * <li>Default method</li>
 * <li>Invoking bean using SpEL</li>
 * 
 * @author amit
 *
 */
public interface PatientProjection extends PatientNameProjection {

	public Gender getGender();

	/**
	 * we can use SpEL to define new/custom values. New values can be build from
	 * target object using SpEL.
	 * 
	 * @return
	 */
	@Value(value = "#{target.firstName + ' ' + target.lastName}")
	public String getFullName();

	/**
	 * We can add logic while getting new value (In cases where logic can not fit in
	 * SpEL)
	 * 
	 * @return
	 */
	public default String getFullNameWithSalutation() {
		StringBuilder nameBuilder = new StringBuilder(getGender().equals(Gender.MALE) ? "Mr" : "Mrs");
		nameBuilder.append(" ").append(getFirstName()).append(" ").append(getLastName());
		return nameBuilder.toString();
	}

	/**
	 * We can add logic while getting existing value (In cases where logic can not
	 * fit in SpEL)
	 * 
	 * @return
	 */
	public default String getGenderAsAString() {
		Gender gender = getGender();
		return gender != null ? gender.toString() : null;
	}

	/**
	 * If we want to externalize the logic to bean, we can use SpEL to invoke bean
	 * method with target.
	 * 
	 * @return Age calculated from external bean.
	 */
	@Value("#{@utils.getAgeFromDob(target.dob)}") // we can pass property of target to bean method
	// @Value("#{@utils.getAgeFromDob(target.dob)}") // we can pass entire targer
	// object to bean method
	public Integer getAge();

}
