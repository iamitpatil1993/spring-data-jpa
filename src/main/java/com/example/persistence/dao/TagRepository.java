package com.example.persistence.dao;

import com.example.persistence.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer>, CustomTagRepository<Tag>  {
}
