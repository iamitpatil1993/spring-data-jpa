/**
 * 
 */
package com.example.persistence.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

/**
 * This class act as a base class for aALL repositories in application context.
 * The proxy that spring creates i.e repository implementation class that spring
 * creates for our Repository interfaces, by default extends SImpleJpaRepository
 * class, but if we want to add some custom methdods to all repositories, then
 * instead of creating custom implementation class for all repositories and
 * adding in them, we can create this class, which will act as a repository base
 * class for all repositories in application. All repository implementations
 * spring will create will be based on this class.
 * 
 * NOTE: We can only override exiting methods in super class and can not add
 * additonal methods, since those additional methods won't be accessible via
 * interface/proxy that we will use in clients of repository.
 * 
 * @author amit
 *
 */
public class CustomJpaRepositoryBase<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomJpaRepositoryBase.class);
	private EntityManager entityManager;

	public CustomJpaRepositoryBase(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	/**
	 * we cann only customize the existing methods in SimpleJpaRepositories, we can
	 * not add new methods here, because we can not access additional methods
	 * declared in these class, because we will get proxy object in classes where we
	 * will inject repositories.
	 */
	@Override
	public <S extends T> S save(S entity) {
		LOGGER.debug("This customization in base repository will be shared/affected by all repositories in applcation");
		return super.save(entity);
	}
}
