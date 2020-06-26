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

  @Field(name="email")
  String email;

  @Field(name="university")
  String university;

  @Field(name="description")
  String description;

  @Field(name="image")
  String image;

// these fields are going to be implemented in the future.

//   @Field(name="saved-events")
//   HashSet<String> savedEvents;

//   @Field(name="saved-organizations")
//   HashSet<String> savedOrganizations;

  public User(Long timestamp, String firstName, String lastName, String email, String university, String description, String image) {
    this.timestamp = timestamp;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.university = university;
    this.description = description;
    this.image = image;
    // savedEvents = new HashSet<String>();
    // savedEvents.add("event1");
    // savedEvents.add("event2");
    // savedEvents.add("event3");
    // savedOrganizations = new HashSet<String>();
    // savedOrganizations.add("ACM"); 
    // savedOrganizations.add("ACM2");
    // savedOrganizations.add("ACM3");
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

//   public String toString() {
//       StringBuilder s = new StringBuilder();
//       s.append(firstName + "\n");
//       s.append(lastName + "\n");
//       s.append(email + "\n");
//       s.append(description + "\n");
//       s.append(university + "\n");
//       s.append(image + "\n");

//       return s.toString();
//   }
  
//   public HashSet<String> getSavedEvents() {
//       return savedEvents;
//   }

//   public HashSet<String> getSavedOrganizations() {
//       return savedOrganizations;
//   }
}
