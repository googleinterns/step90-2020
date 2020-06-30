package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import java.util.*;

@Entity(name = "organization")
public class Organization {
  @Id
  Long datastoreId;

  Long timestamp;

  String name;

  String email;

  String university;

  @Field(name="user-type")
  String userType;
  String description;
  String image;
  
  public Organization() {

  }
  
  public Organization(Long timestamp, String name, String email, String university, String userType, String description, String image) {
    this.timestamp = timestamp;
    this.name = name;
    this.email = email;
    this.university = university;
    this.userType = userType;
    this.description = description;
    this.image = image;
  }

  public Organization(Long datastoreId, Long timestamp, String name, String email, String university, String userType, String description, String image) {
    this.datastoreId = datastoreId;
    this.timestamp = timestamp;
    this.name = name;
    this.email = email;
    this.university = university;
    this.userType = userType;
    this.description = description;
    this.image = image;
  }
  
  public Long getDatastoreId() {
    return datastoreId;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getUniversity() {
    return university;
  }

  public String getUserType() {
    return userType;
  }

  public String getDescription() {
    return description;
  }

  public String getImage() {
    return image;
  }
  
}
