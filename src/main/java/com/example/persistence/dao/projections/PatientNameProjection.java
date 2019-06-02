/**
 * 
 */
package com.example.persistence.dao.projections;

/**
 * Projection interface for patient to get subset of paient details (only names)
 * We will use this interface as a return type of query method, and spring will
 * handle everything
 * <li>Spring will create instance(proxy) of this interface for each entity
 * selected in query</li>
 * <li>On calling method on proxy of this interface, spring will forward call to
 * target entity object, this is in order to apply any additional logic in
 * getter of attribute</li>
 * <li>We do not need to create implementation of this interface</li>
 * <li>This interface can only has getter methods for attributes in target
 * object.</li>
 * <li>Signature of these getters must match with those in target object.</li>
 * 
 * @author amit
 *
 */
public interface PatientNameProjection {

	public Integer getId();

	public String getFirstName();

	public String getLastName();
}
