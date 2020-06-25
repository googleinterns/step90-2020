package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import java.util.*;

@Entity(name = "user")
public class User {
  @Id
  Long datastoreId;

  Long timestamp;

  @Field(name="firstname")
  String firstName;

  @Field(name="lastname")
  String lastName;

  String email;

  String university;
  String description;
  String image;
  ArrayList<String> savedEvents;
  ArrayList<Organization> savedOrganizations;

  public User(Long timestamp, String firstName, String lastName, String email, String university, String description, String image) {
    this.timestamp = timestamp;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.university = university;
    this.description = description;
    this.image = image;
    // savedEvents = new ArrayList<Event>(Arrays.asList({"event1", "event2", "event3"}));
    // savedOrganizations = new ArrayList<Organization>();
    // savedOrganizations.add(new Organization(System.currentTimeMillis(), "ACM", "acm@gmail.com", "university", "hello", ""); 
    // savedOrganizations.add(new Organization(System.currentTimeMillis(), "ACM2", "acm@gmail.com", "university2", "hello2", "");
    // savedOrganizations.add(new Organization(System.currentTimeMillis(), "ACM3", "acm@gmail.com", "university3", "hello3", "");
  }
  
  public Long getDatastoreId() {
    return datastoreId;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public String getUniversity() {
    return university;
  }

  public String getDescription() {
    return description;
  }

  public String getImage() {
    return image;
  }
  
  public ArrayList<String> getSavedEvents() {
      return savedEvents;
  }

  public ArrayList<Organization> getSavedOrganizations() {
      return savedOrganizations;
  }
}
