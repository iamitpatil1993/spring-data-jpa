/**
 * 
 */
package com.example.persistence.dto;

/**
 * DTO to be used for projections in query methods.
 * 
 * @author amit
 *
 */
public class PatientNameDto {

	private String firstName;

	private String lastName;

	/**
	 * <li>We must define constructor</li>
	 * <li>The fields to be loaded are determined from the parameter names of the
	 * constructor that is exposed</li>
	 * 
	 * <li>We must define constructor will all fields we want to fetch, spring will
	 * only fetch fields which are in constructor arguments.</li>
	 * 
	 * <li>If we do not define any constructor spring will give error as
	 * org.springframework.core.convert.ConverterNotFoundException</li>
	 * 
	 * <li>We can not overload constructor, i.e we can not define multiple
	 * constructors as it will create ambiguity, in that case as well spring will
	 * give org.springframework.core.convert.ConverterNotFoundException</li>
	 * 
	 * <li>Unlike interface based projections, we can not define nested
	 * (associations) here. We can only fetch aggregate root entity and can not
	 * fetch nested/associated single value association, which we can do using
	 * interface based projections.</li>
	 * 
	 * <li>It is exactly similar to POJO based projectons that we can do in JPQL
	 * using NEW keyword, except we can explicitly call constructor in jpql and can
	 * map any field (associated entity as well) as long as matching constructor
	 * found in POJO, but we can not do that here, only single constructor, and
	 * fields to select is determined from constructor argume and we can not
	 * customize anything.</li>
	 * 
	 */
	public PatientNameDto(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

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

}
