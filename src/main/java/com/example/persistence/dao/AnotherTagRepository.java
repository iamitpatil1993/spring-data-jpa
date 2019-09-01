package com.example.persistence.dao;

import com.example.persistence.model.AnotherTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnotherTagRepository extends JpaRepository<AnotherTagRepository, Integer>, CustomTagRepository<AnotherTag>  {
}
