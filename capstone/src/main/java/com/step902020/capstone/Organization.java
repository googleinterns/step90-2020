package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import java.util.*;

@Entity(name = "organization")
public class Organization implements Comparable {
  @Id
  Long datastoreId;

  Long timestamp;

  String name;

  String email;

  @Reference
  University university;

  @Field(name="user-type")
  String userType;

  String description;
  Integer rank;

  @Field(name="org-type")
  String orgType;

  @Reference
  TreeSet<Event> events;
  
  @Reference
  List<Review> reviews;
  
  public Organization() {
  }

  public Organization(Long timestamp, String name, String email, University university, String userType,
                      String description, String orgType) {
    this.timestamp = timestamp;
    this.name = name;
    this.email = email;
    this.university = university;
    this.userType = userType;
    this.description = description;
    this.events = new TreeSet<Event>();
    this.reviews = new ArrayList();
    this.orgType = orgType;
    rank = 0;
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

  public University getUniversity() {
    return university;
  }

  public String getUserType() {
    return userType;
  }

  public String getDescription() {
    return description;
  }

  public String getOrgType() {
    return orgType;
  }
  
  public TreeSet<Event> getEvents() {
    return events;
  }

  public List<Review> getReviews() {
    return reviews;
  }

  public Integer getRank() {
    return rank;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setOrgType(String orgType) {
    this.orgType = orgType;
  }

  public void addEvent(Event event) {
    events.add(event);
  }

  /**
   * increase rank by 1
   */
  public void incrementRank() {
    rank++;
  }

  /**
   * decrease rank by 1
   */
  public void decrementRank() {
    rank--;
  }

  public void deleteEvent(Event event) {
    events.remove(event);
  }

  /**
   * returns organization information in a string format
   * @return String
   */
  public String toString() {
    return name + " " + datastoreId;
  }

  /**
   * implement equality
   * @param o object being compared to
   * @return boolean
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Organization)) {
      return false;
    }

    Organization organization = (Organization) o;
    return this.datastoreId.equals(organization.datastoreId);
  }

  @Override
  public int hashCode() {
    return datastoreId.hashCode();
  }

  /**
   * returns how other object compares to this object
   * @param o object being compared
   * @return negative if current object is smaller,
   * zero if current object is equal, positive if current object is bigger
   */
  @Override
  public int compareTo(Object o) {
    if (o == this) {
      return 0;
    }
    if (!(o instanceof Organization)) {
      return -1;
    }
    Organization other = (Organization) o;
    if (this.getName() == null || other.getName() == null) {
      return -1;
    }
    return this.getName().toLowerCase().compareTo(other.getName().toLowerCase());
  }

  /**
   * Add new review to list
   * @param review Review object
   */
  public void addReview(Review review) {
    reviews.add(review);
  }
}
