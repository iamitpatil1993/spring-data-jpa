/**
 * 
 */
package com.example.persistence.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.example.persistence.model.Encounter;

/**
 * Plain jdbc implementation of EncounterDao
 * 
 * @author amit
 *
 */
@Repository
public class JdbcEncounterDao implements EncounterRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(JdbcEncounterDao.class);
	private SimpleJdbcInsert jdbcInsert;
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public JdbcEncounterDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Encounter add(Encounter encounter) {
		jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
				.withTableName("encounter");
		jdbcInsert.compile();
		
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		String encounterId = UUID.randomUUID().toString();
		mapSqlParameterSource.addValue("encounter_id", encounterId);
		mapSqlParameterSource.addValue("patient_id", encounter.getPatientId());
		mapSqlParameterSource.addValue("created_timestamp", encounter.getCreatedTimestamp());
		mapSqlParameterSource.addValue("updated_timestamp", encounter.getUpdatedTimestap());

		jdbcInsert.execute(mapSqlParameterSource);
		LOGGER.info("Encounter created with encounterId :: {}, patientId :: {}", encounterId, encounter.getPatientId());
		
		encounter.setEncounterId(encounterId);
		return encounter;
	}

	@Override
	public Optional<Encounter> findById(String encounterId) {
		try {
			Encounter encounter = jdbcTemplate.queryForObject("SELECT * FROM encounter WHERE encounter_id = ?", new EncounterMapper(), encounterId);
			return Optional.of(encounter);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}
	
	public static class EncounterMapper implements RowMapper<Encounter> {

		@Override
		public Encounter mapRow(ResultSet rs, int rowNum) throws SQLException {
			Encounter encounter = new Encounter();
			encounter.setEncounterId(rs.getString("encounter_id"));
			encounter.setPatientId(rs.getInt("patient_id"));
			
			Calendar createdTimestamp = Calendar.getInstance();
			createdTimestamp.setTimeInMillis(rs.getTimestamp("created_timestamp").getTime());
			encounter.setCreatedTimestamp(createdTimestamp);

			Calendar updatedTimestamp = Calendar.getInstance();
			updatedTimestamp.setTimeInMillis(rs.getTimestamp("updated_timestamp").getTime());
			encounter.setUpdatedTimestap(updatedTimestamp);
			return encounter;
		}
	}

}
