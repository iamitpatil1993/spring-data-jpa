/**
 * 
 */
package com.example.persistence.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

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
	
	@Autowired
	private TaskExecutor executor;

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
	
	@Transactional
	public void saveInBatch(List<Patient> patients) {
		int batchSize = 100;
		
		List<Patient> currentBatch = new ArrayList<>(batchSize);
		for (int i = 1; i <= patients.size(); i++) {
			currentBatch.add(patients.get(i -1 ));
			if (i % batchSize == 0) {
				batchInsertAsync(currentBatch);
				currentBatch = new ArrayList<>(batchSize); // creating new instance instead of cleare, due to concurrent modification issue.
			}
		}
	}

	/**
	 * Even tough method is called privately and not via Proxy, it will still participate in active transaction,
	 * since EntityManager is attached to thread local and EntityManagerFactoryUtils will provide EM attached to thread local
	 */
	private void batchInsertAsync(List<Patient> currentBatch) {
		// executes code asynchronously and return immediately without waiting for code in Runnable completes
		executor.execute(() -> {
			patientRepository.saveAll(currentBatch);
			entityManager.clear();
		});
	}

	private void doSomethingWithData(Slice<Patient> slice) {
		try {
			LOGGER.debug("Processing records from page with Page Number :: {}", slice.getPageable().getPageNumber());
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			LOGGER.error("Error while processing data", e);
		}
	}
	
	/**
	 * We can run any method asynchronously
	 * NOTE: return type of Async methods can be either of void, Future<>, ListenableFuture
	 */
	@Async(value = "threadPoolTaskExecutor") // no need to specify here if configured in config file.
	public void doSomethingAsynchronously(int sleepMiliSeconds) {
		LOGGER.debug("Sleeping ...");
		try {
			Thread.sleep(sleepMiliSeconds);
		} catch (InterruptedException e) {
			LOGGER.error("Something went wrong while async processing", e);
		}
		LOGGER.debug("Woke up ...");
	}
	
	/**
	 * We can run any method asynchronously
	 * NOTE: return type of Async methods can be either of void, Future<>
	 * 
	 * Returning Future to pass data to caller. Data must be wrapped inside Future.
	 * We can not return data directly.
	 */
	@Async(value = "threadPoolTaskExecutor") // no need to specify here if configured in config file.
	public Future<String> doSomethingAsynchronouslyAndReturnData(int sleepMiliSeconds) {
		CompletableFuture<String> future = new CompletableFuture<>();
		LOGGER.debug("Sleeping ...");
		try {
			Thread.sleep(sleepMiliSeconds);
		} catch (InterruptedException e) {
			LOGGER.error("Something went wrong while async processing", e);
		}
		LOGGER.debug("Woke up ...");
		future.complete("Completed Successfully");
		return future;
	}
	
	/**
	 * We can return spring special interface and specialization on java.util.concurrent.Future.
	 * ListenableFuture.
	 * Unlike java Future, we can add callback handlers (Same as we do in vert.x) and appropriate callbacks
	 * will get called on success/fail cases.
	 * 
	 * Unlike java Future (Future#get), Caller won't have to block to get data, we can define callback
	 * and get data in callback and process it.)
	 */
	@Async(value = "threadPoolTaskExecutor") // no need to specify here if configured in config file.
	public ListenableFuture<String> doSomethingAsynchronouslyWithCallback(int sleepMiliSeconds) {
		LOGGER.debug("Sleeping ...");
		try {
			Thread.sleep(sleepMiliSeconds);
		} catch (InterruptedException e) {
			LOGGER.error("Something went wrong while async processing", e);
		}
		LOGGER.debug("Woke up ...");
		return new AsyncResult<String>("Completed Successfully");
	}
	
	@Transactional
	public void updatePatientName(final String firstName, final String lastName, final Integer id) {
		Optional<Patient> patientOptional = patientRepository.findById(id);
		if (!patientOptional.isPresent()) {
			throw new RuntimeException("No record found by Id");
		}
		Patient patient = patientOptional.get();
		patient.setFirstName(firstName.concat(UUID.randomUUID().toString()));
		patient.setLastName(lastName.concat(UUID.randomUUID().toString()));

		// since we disabled flushing before query execution using
		// @Modifying(flushAutomatically = false), spring
		// will NOT flush persistence context before executing query.
		// Also note, we have set query hint to indicate persistence provider to not to
		// flush persistence context,
		// before query execution and should flush at the time of commit.
		// So, neither spring nor JPA persistence provider will flush
		// Persistence context before executing query. Hence name updated in below
		// update query will be overridden by changes in persistence context, after
		// flush operation performed by
		// hibernate at commit Time

		patientRepository.updateNameWithoutFlushingExistinChanges(firstName, lastName, id);
	}
	
	@Transactional
	public void updatePatientNameWithFlushBeforeQueryExection(final String firstName, final String lastName,
			final Integer id) {
		Optional<Patient> patientOptional = patientRepository.findById(id);
		if (!patientOptional.isPresent()) {
			throw new RuntimeException("No record found by Id");
		}
		Patient patient = patientOptional.get();
		patient.setFirstName(firstName.concat(UUID.randomUUID().toString()));
		patient.setLastName(lastName.concat(UUID.randomUUID().toString()));

		// since we enabled flushing before query execution using
		// @Modifying(flushAutomatically = true), spring
		// will flush persistence context before executing query.
		// Also note, we have set query hint to indicate persistence provider to not to
		// flush persistence context,
		// before query execution and should flush at the time of commit. So, spring is
		// flushing persistence context
		// before update query.
		patientRepository.updateNameWithFlushingExistinChanges(firstName, lastName, id);
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
