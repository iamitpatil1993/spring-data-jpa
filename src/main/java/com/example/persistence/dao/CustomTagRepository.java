package com.example.persistence.dao;

import com.example.persistence.model.SuperTag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomTagRepository<T extends SuperTag<?>> {

    List<T> findAllTags();
}
