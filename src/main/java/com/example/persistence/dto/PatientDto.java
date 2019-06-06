/**
 * 
 */
package com.example.persistence.dto;

import java.util.Calendar;

import com.example.persistence.model.Gender;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Demonstrates use of Project Lombok to create POJO/DTO. We do not need to add
 * anything like constructors, toString, equals, hashCode, getters, setters. We
 * can use annotations in project lombok, to handle all these things. We just
 * need to define pojo fields and appropriate annotations. It generated code at
 * compile time.
 * 
 * @author amit
 *
 */
@Data // this creates Getters (not setters, so makes oobject Immutable) for all final
		// fields along with toString, equals, hashcode.
@AllArgsConstructor(access = AccessLevel.PUBLIC) // In order to use DTO based projections, we must have single
													// constructor with all fields that we want to fetch with
													// constructor argument names matching exactly to entity attributes.
													// So, using Project Lombok, annotation to create constructor for us
													// with all fields.
public class PatientDto {

	private Integer id;

	private String firstName;

	private String lastName;

	private Calendar dob;

	private String ssn;

	private String bloodGroup;

	private Gender gender;

}
