/**
 * 
 */
package com.example.persistence.dao;

import java.util.Calendar;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import com.example.persistence.model.BaseEntity;

/**
 * This interface act as a base interface for all JPA repositories defined in
 * application. This interface defined method on BaseEntity mapped super class,
 * so all entities that extends BaseEntity, will get these methods. We do not
 * want to repeat methods related to base entiy in all repositories, so in this
 * way we can create base repository interface to define query methods common to
 * all entities as well as query methods related to propeties in BaseEntity.
 * 
 * This repository inerface defines Generic entity type that extends BaseEntity because, we wanted to define queries on BaseEntity properties.
 * And hence only entities that extends BaseEntity can be used as a concrete type.
 * 
 * Obiviously, we can remove extends and can define generic query methods for all repositories, BUT you do not know properties of concrete entity type
 * of repositories that will extends this.
 * Therefore, thise intermediate repository mostly useful (and make sense) to define query methods on base entity types if we have in application.  
 * @author amit
 *
 */
@NoRepositoryBean // this annotation says, do not create repository implememation bean for this repository interface.
public interface BaseEntityRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {

	/**
	 * Spring has limited support for SpL in spring data JPA. we can use
	 * #{#entityName} to refer to Entity to which repository interface associated
	 * to. this is how we can define queries on BaseEntity properties.
	 * Refer for more information
	 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query.spel-expressions 
	 */
	@Query(value = "SELECT createdDate FROM #{#entityName} WHERE id = ?1")
	public Optional<Calendar> getCreatedDateById(final ID id);

	/**
	 * we can define Generic entity queries as well.
	 */
	@Query(value = "SELECT COUNT(id) = 1 FROM #{#entityName} WHERE id = ?1")
	public boolean isExisById(final ID id);

}
