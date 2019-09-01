package com.example.persistence.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tag")
public class Tag extends SuperTag<Tag> {

}
