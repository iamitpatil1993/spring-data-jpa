/**
 * 
 */
package com.example.persistence.dao;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.persistence.model.Patient;
import com.example.persistence.model.QPatient;
import com.example.persistence.model.VitalType;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;


/**
 * This is custom Patient repository, which adds custom functionality to
 * spring-data-jpa auto generated JpaRepository (PatientRepository)
 * 
 * We need to create custom interface (here CustomPatientRepository) in order define custom repository.
 * @author amit
 *
 */
// Name of this class can be CustomRepositoryInterfaceName + Impl. Spring will automatically add this to base PatientRepository.
@Repository
public class CustomPatientRepositoryImpl implements CustomPatientRepository, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomPatientRepositoryImpl.class);
	private EntityManager em;

	@Autowired
	public CustomPatientRepositoryImpl(EntityManager em) {
		this.em = em;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.example.persistence.dao.CustomPatientRepository#findAllDiabeticPatients()
	 */
	@Transactional(readOnly = true)
	@Override
	public List<Patient> findAllDiabeticPatients() {
		String query = "SELECT p FROM Patient p WHERE p.id IN (SELECT pv.patient.id FROM PatientVital pv WHERE isDeleted = false AND vital = :vitalType AND value >= 150)";
		return em.createQuery(query, Patient.class).setParameter("vitalType", VitalType.SUGAR).getResultList();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		assert em != null : "Entity manager required to be injected to use repository";
	}

	/**
	 * Native use of QueryDsl without any spring specific integration.
	 */
	@Transactional(readOnly = true)
	@Override
	public Optional<Patient> findByPatientIdUsingQueryDsl(Integer patientId) {
		// I think we should always create this JpqQuery instance since it is depends on EM. And EM is specific to
		// transaction and attahed to thread local. So, we can not reuse this JpqQuery instance.
		// Also I think, if want to use QueryFactory object to create query over direct instantiation of JpqQuery, we need to 
		// create QueryFactory every time and can not share or make singleton because, it also depends on EM, and EM is 
		// specific to transaction and attached to thread local.
		JPQLQuery<Patient> query = new JPAQuery<>(em); 
		QPatient qPatient = QPatient.patient;
		Patient patient = query.from(qPatient).where(qPatient.id.eq(patientId).and(qPatient.isDeleted.eq(false))).fetchOne();
		return Optional.ofNullable(patient);
	}//marketplace.eclipse.org/marketplace-client-intro?mpc_install=219993

	/**
	 * Ordering in Query DSL
	 */
	@Transactional(readOnly = true)
	@Override
	public List<Patient> getPatientsCreatedInLastMonth() {
		Calendar startDate = Calendar.getInstance();
		startDate.add(Calendar.MONTH, -1);
		JPAQuery<Patient> jpaQuery = new JPAQuery<>(em);
		QPatient patient = QPatient.patient;
		List<Patient> patients = jpaQuery.from(patient)
				.where(patient.createdDate.after(startDate).or(patient.createdDate.eq(startDate)))
				.orderBy(patient.createdDate.desc()).fetch();
		return patients;
	}
	
	@Transactional(readOnly = true)
	@Override
	public Optional<Calendar> findYungestPatient() {
		JPAQuery<Calendar> jpaQuery = new JPAQuery<>(em);
		QPatient patient = QPatient.patient;
		Calendar yungestPatientDob = jpaQuery.select(patient.dob.min()).from(patient).where(patient.isDeleted.eq(false)).fetchOne();
		return Optional.ofNullable(yungestPatientDob);
	}
	
	@Transactional(readOnly = true)
	@Override
	public Map<String, Calendar> findYungestPatientGroupByBloodGroup() {
		JPAQuery<Calendar> jpaQuery = new JPAQuery<>(em);
		QPatient patient = QPatient.patient;
		List<Tuple> tuples = jpaQuery.select(patient.bloodGroup, patient.dob.min().as("minDob")).from(patient)
				.where(patient.isDeleted.eq(false)).groupBy(patient.bloodGroup).fetch();
		Map<String, Calendar> bloodGroupDobMap = tuples.stream().collect(Collectors
				.toMap((Tuple tuple) -> tuple.get(0, String.class), (Tuple tuple) -> tuple.get(1, Calendar.class)));
		return bloodGroupDobMap;
	}

}
