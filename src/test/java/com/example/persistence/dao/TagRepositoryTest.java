package com.example.persistence.dao;

import com.example.persistence.BaseTest;
import com.example.persistence.model.AnotherTag;
import com.example.persistence.model.Post;
import com.example.persistence.model.PostComment;
import com.example.persistence.model.Tag;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PostRemove;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@Transactional
public class TagRepositoryTest extends BaseTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AnotherTagRepository anotherTagRepository;


    @Test
    public void test() {
        final List allTags = tagRepository.findAllTags();
        System.out.println("allTags = " + allTags);

    }
}