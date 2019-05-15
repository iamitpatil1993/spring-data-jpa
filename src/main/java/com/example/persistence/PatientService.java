/**
 * 
 */
package com.example.persistence;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.persistence.dao.EncounterRepository;
import com.example.persistence.dao.auto.PatientRepository;
import com.example.persistence.model.Encounter;
import com.example.persistence.model.Patient;

/**
 * @author amit
 *
 */
@Service
public class PatientService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PatientService.class);
	private PatientRepository patientRepository = null;
	private EncounterRepository encounterRepository = null;
	
	@PersistenceContext
	private EntityManager entityManager; // Ideally EntityManager is not concern of service layer, but add just to validate tests/ 

	public PatientService(PatientRepository patientRepository, EncounterRepository encounterRepository) {
		this.patientRepository = patientRepository;
		this.encounterRepository = encounterRepository;
	}
	
	@Transactional
	public void doSomethingInTransaction() {
		Patient patient = createTestPatient();
		patientRepository.save(patient);
		
		patientRepository.deleteAllInBatch(); // It execute single sql, "DELTE FROM TABLE" and BUT DO NOT detach entities.
		assert entityManager.contains(patient); // entity is still managed
	}
	
	@Transactional
	public void deletePatientsInBatch(List<Patient> patients) {
		patients.stream().forEach(patient -> {
			patientRepository.save(patient);
		});
		
		patientRepository.deleteInBatch(patients); // It execute single sql, "DELTE FROM TABLE" and BUT DO NOT detach entities.
		
		patients.stream().forEach(patient -> {
			assert entityManager.contains(patient); // entity is still managed	
		});
	}
	
	@Transactional
	public int updatePatientSsnByPatienId(Patient patient) {
		return patientRepository.updateSsnByPatientId(patient.getId(), patient.getSsn());
	}
	
	@Transactional
	public Encounter doSomethingInTransaction(final Patient patient, Encounter encounter) {
		// create Encounter using JDBC repository
		Encounter createdEncounter = encounterRepository.add(encounter);
		
		// create patient using JPA repository.
		patient.setUpdatedDate(Calendar.getInstance());
		patientRepository.save(patient);
		
		return createdEncounter;
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
