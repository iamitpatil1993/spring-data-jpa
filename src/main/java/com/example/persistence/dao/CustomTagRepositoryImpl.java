package com.example.persistence.dao;

import com.example.persistence.model.SuperTag;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class CustomTagRepositoryImpl<T extends SuperTag<?>>  implements CustomTagRepository<T> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<T> findAllTags() {
        final Query query = entityManager.createQuery("SELECT t FROM Tag t");
        return query.getResultList();
    }
}
