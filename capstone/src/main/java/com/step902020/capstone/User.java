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

  @Field(name="user-type")
  String userType;

  @Field(name="image")
  String image;

  public User() {
  }

  public User(Long timestamp, String firstName, String lastName, String email, String university, String userType, String image) {
    this.timestamp = timestamp;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.university = university;
    this.userType = userType;
    this.image = image;
  }

  public User(long datastoreId, Long timestamp, String firstName, String lastName, String email, String university, String userType, String image) {
    this.datastoreId = datastoreId;
    this.timestamp = timestamp;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.university = university;
    this.userType = userType;
    this.image = image;
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

  public String getUserType() {
    return userType;
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
