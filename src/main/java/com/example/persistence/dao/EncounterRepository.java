/**
 * 
 */
package com.example.persistence.dao;

import java.util.Optional;

import com.example.persistence.model.Encounter;

/**
 * @author amit
 *
 */
public interface EncounterRepository {
	
	public Encounter add(final Encounter encounter);
	
	public Optional<Encounter> findById(final String encounterId);
	
}
