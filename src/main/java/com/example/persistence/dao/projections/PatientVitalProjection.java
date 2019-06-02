/**
 * 
 */
package com.example.persistence.dao.projections;

import com.example.persistence.model.PatientVital;
import com.example.persistence.model.VitalType;

/**
 * Patient vital projection to fetch vital details only. We can use Projection
 * for nested properties (associations) as well. Here we are using
 * PatientNamerojection to fetch projection, because we are interested only in
 * name of patient.
 * 
 * @author amit
 *
 */
public interface PatientVitalProjection {

	public VitalType getVital();

	public Double getValue();

	/**
	 * We can use Projections for nested properties (associations). NOTE: No matter
	 * what you select here (return type) projection with single attribute,
	 * projections with multiple attributes, selecting Patient entity directly,
	 * spring can not optimize it, i.e spring needs to fetch all patient details.
	 * 
	 * Even if we use projections with subset of patient properties, spring will
	 * always fetch all properties from database, because accessing
	 * {@link PatientVitalProjection#getPatient()}, spring first gets Patient
	 * (object) object by forwarding to {@link PatientVital#getPatient()}, and then
	 * creates proxy object by wrapping that patient object. So, while creating
	 * proxy no matter what details required, spring first directly gets patient
	 * from {@link PatientVital#getPatient()}, which will be always with all
	 * details, and creates proxy with subset of details required for Projection
	 * interface declared.
	 * 
	 * @return
	 */
	public PatientNameProjection getPatient();
	//public Patient getPatient(); will also fetch entire patient.
	//public AnyOtherPatientProjection getPatient(); will also fetch entire patient.
	

}
