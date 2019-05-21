/**
 * 
 */
package com.example.persistence.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;
import org.springframework.util.concurrent.ListenableFuture;

import com.example.persistence.BaseTest;
import com.example.persistence.dao.auto.PatientRepository;
import com.example.persistence.dao.auto.PatientRepositoryTest;
import com.example.persistence.model.Patient;

/**
 * @author amit
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PatientServiceTest extends BaseTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(PatientServiceTest.class);
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private PatientRepository patientRepository;
	
	/**
	 * Test method for {@link com.example.persistence.service.PatientService#stressTestUsingPagination()}.
	 */
	@Test
	public void testStressTestUsingPagination() {
		patientService.stressTestUsingPagination();
	}
	
	/**
	 * Test method for {@link com.example.persistence.service.PatientService#stressTestUsingStream()}.
	 */
	@Test
	public void testStressTestUsingStream() {
		patientService.stressTestUsingStream();
	}
	
	/**
	 * Test method for {@link com.example.persistence.service.PatientService#saveInBatch()}.
	 */
	@Test
	public void testSaveInBatch() {
		int count = 1000;
		List<Patient> patients = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			patients.add(PatientRepositoryTest.createTestPatient());
		}
		patientService.saveInBatch(patients);
	}
	
	@Test
	public void testDoSomethingAsynchronously() {
		// give
		int sleepMiliSeconds = 5000;
		StopWatch stopWatch = new StopWatch();
		
		// when
		stopWatch.start();
		patientService.doSomethingAsynchronously(sleepMiliSeconds);
		stopWatch.stop();
		
		// then
		assertThat(stopWatch.getLastTaskTimeMillis(), is(lessThan(new Long(sleepMiliSeconds))));
	}
	
	
	@Test
	public void testDoSomethingAsynchronouslyAndReturnData() {
		// give
		int sleepMiliSeconds = 5000;
		StopWatch stopWatch = new StopWatch();
		
		// when
		stopWatch.start();
		Future<String> future = patientService.doSomethingAsynchronouslyAndReturnData(sleepMiliSeconds);
		stopWatch.stop();
		
		// then
		assertThat(stopWatch.getLastTaskTimeMillis(), is(lessThan(new Long(sleepMiliSeconds))));
		assertThat(future.isDone(), is(false));
		// future.get(); // this will be blocking this thread and will wait until async task complete, use ListenableFuture instead.
	}
	
	@Test
	public void testDoSomethingAsynchronouslyWithCallback() {
		// give
		int sleepMiliSeconds = 5000;
		StopWatch stopWatch = new StopWatch();
		
		// when
		stopWatch.start();
		ListenableFuture<String> future = patientService.doSomethingAsynchronouslyWithCallback(sleepMiliSeconds);
		stopWatch.stop();
		
		// then
		// define callback, which will get called on task completion (success/fail)
		future.addCallback(data -> {
			assertThat(data, is(notNullValue()));
			LOGGER.debug("data recived in listenable callback is :: {}", data);
		}, exception -> {
			exception.printStackTrace();
		});
		
		// assert method executed asynchronously
		assertThat(stopWatch.getLastTaskTimeMillis(), is(lessThan(new Long(sleepMiliSeconds))));
		assertThat(future.isDone(), is(false));
	}

}
