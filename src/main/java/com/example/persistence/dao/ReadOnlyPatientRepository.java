/**
 * 
 */
package com.example.persistence.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.persistence.model.Patient;

/**
 * This interface defines read-only patient repository, by limiting methods
 * exposed by Crud repository. In this way, we can expose selective
 * functionality from spring Repository.
 * 
 * Here we just copy pasted required methods from CrudReposiory, spring will
 * automatically understand from signature of method that these methods are from
 * Crudrepository and provides implementation.
 * 
 * @author amit
 *
 */
public interface ReadOnlyPatientRepository extends CrudRepository<Patient, Integer> {

	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id or {@literal Optional#empty()} if none
	 *         found
	 * @throws IllegalArgumentException if {@code id} is {@literal null}.
	 */
	Optional<Patient> findById(Integer id);

	/**
	 * Returns whether an entity with the given id exists.
	 *
	 * @param id must not be {@literal null}.
	 * @return {@literal true} if an entity with the given id exists,
	 *         {@literal false} otherwise.
	 * @throws IllegalArgumentException if {@code id} is {@literal null}.
	 */
	boolean existsById(Integer id);

	/**
	 * Returns all instances of the type.
	 *
	 * @return all entities
	 */
	Iterable<Patient> findAll();

	/**
	 * Returns all instances of the type with the given IDs.
	 *
	 * @param ids
	 * @return
	 */
	Iterable<Patient> findAllById(Iterable<Integer> ids);

	/**
	 * Returns the number of entities available.
	 *
	 * @return the number of entities
	 */
	long count();
}
