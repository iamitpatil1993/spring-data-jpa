/**
 * 
 */
package com.example.persistence;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.persistence.crm.dao.CaseRepository;
import com.example.persistence.crm.model.Case;
import com.example.persistence.crm.model.CaseStatus;
import com.example.persistence.dao.auto.PatientRepository;
import com.example.persistence.model.Patient;

/**
 * @author amit
 *
 */
@Service
public class PatientService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PatientService.class);
	private PatientRepository patientRepository = null;
	private CaseRepository caseRespository = null;
	
	/**
	 * Sine there are two EntityManagerFactories for two persistence units in application, we need to specify
	 * which EntitymanagerFactory to use to get EntityManager.
	 */
	@PersistenceContext(unitName = "foo-unit")
	private EntityManager entityManager; // Ideally EntityManager is not concern of service layer, but add just to validate tests/ 

	public PatientService(PatientRepository patientRepository, CaseRepository caseRepository) {
		this.patientRepository = patientRepository;
		this.caseRespository = caseRepository;
	}
	
	// since this is custom implementation class, we need to specify
	// transactionManager to use, since there are two in application
	// We do not need to specify this in case of repository classes because in case of repositories, spring 
	// knows which entity manager to use via @EnableJpaRepositoties annotation attributes.
	@Transactional(transactionManager = "transactionManager")
	public void doSomethingInTransaction() {
		Patient patient = createTestPatient();
		patientRepository.save(patient);
		
		Case case1 = new Case();
		case1.setCaseStatus(CaseStatus.IN_PROGRESS);
		case1.setDescription("Gateway timeout error on adding item to cart");
		case1.setOwnerId(UUID.randomUUID().toString());
		caseRespository.save(case1);
		
		patientRepository.deleteAllInBatch(); // It execute single sql, "DELTE FROM TABLE" and BUT DO NOT detach entities.
		assert entityManager.contains(patient); // entity is still managed
	}
	
	// same here
	@Transactional(transactionManager = "transactionManager")
	public void deletePatientsInBatch(List<Patient> patients) {
		patients.stream().forEach(patient -> {
			patientRepository.save(patient);
		});
		
		patientRepository.deleteInBatch(patients); // It execute single sql, "DELTE FROM TABLE" and BUT DO NOT detach entities.
		
		patients.stream().forEach(patient -> {
			assert entityManager.contains(patient); // entity is still managed	
		});
	}
	
	// same here
	@Transactional(transactionManager = "transactionManager")
	public int updatePatientSsnByPatienId(Patient patient) {
		return patientRepository.updateSsnByPatientId(patient.getId(), patient.getSsn());
	}
	
	private Patient createTestPatient() {
		Patient patient = new Patient();
		patient.setFirstName("Bob");
		patient.setLastName("Sargent");
		Calendar dob = Calendar.getInstance();
		dob.add(Calendar.YEAR, -20);
		patient.setDob(dob);
		return patient;
	}
}
