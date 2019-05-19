/**
 * 
 */
package com.example.persistence.service;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	@PersistenceContext
	private EntityManager entityManager; // Ideally EntityManager is not concern of service layer, but add just to validate tests/ 

	public PatientService(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
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

	@Transactional(readOnly = true)
	public void stressTestUsingPagination() {
		int pageSize = 2000;
		
		Pageable pageable = PageRequest.of(0, pageSize);
		Slice<Patient> slice = null;
		do {
			slice = patientRepository.findAll(pageable);
			doSomethingWithData(slice);
			entityManager.clear();
			pageable = slice.nextPageable();
		} while(slice.hasNext());
	}
	
	@Transactional(readOnly = true)
	public void stressTestUsingStream() {
		try (Stream<Patient> patientStream = patientRepository.findAllViaStream()) {
			patientStream.forEach(patient -> {
				try {
					Thread.sleep(500);
					entityManager.detach(patient);
				} catch (InterruptedException e) {
					LOGGER.error("Error while processing data", e);
				}
			});
		} catch (Exception e) {
			LOGGER.error("Error fetcing data", e);
		}
	}

	private void doSomethingWithData(Slice<Patient> slice) {
		try {
			LOGGER.info("Processing records from page with Page Number :: {}", slice.getPageable().getPageNumber());
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			LOGGER.error("Error while processing data", e);
		}
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
