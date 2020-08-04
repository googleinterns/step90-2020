package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Reference;
import org.springframework.data.annotation.Id;

import java.util.*;

@Entity(name = "event")
public class Event implements Comparable{

  @Id
  Long datastoreId;

  @Reference
  University university;

  Long organizationId;

  String eventTitle;

  String eventDateTime;

  String eventDescription;

  double eventLatitude;

  double eventLongitude;

  String eventType;

  String energyLevel;

  String location; // Indoor/Outdoor

  Boolean foodAvailable;

  Boolean free;

  Boolean visitorAllowed;
  
  @Reference
  List<Review> reviews;

  Integer rank;

  public Event(University university, long organizationId, String eventTitle,String eventDateTime, String eventDescription, 
               double eventLatitude, double eventLongitude, String eventType, String energyLevel, String location, 
               Boolean foodAvailable, Boolean free, Boolean visitorAllowed) {
    this.university = university;
    this.organizationId = organizationId;
    this.eventTitle = eventTitle;
    this.eventDateTime = eventDateTime;
    this.eventDescription = eventDescription;
    this.eventLatitude = eventLatitude;
    this.eventLongitude = eventLongitude;
    this.eventType = eventType;
    this.energyLevel = energyLevel;
    this.location = location;
    this.foodAvailable = foodAvailable;
    this.free = free;
    this.visitorAllowed = visitorAllowed;
    this.reviews = new ArrayList();
    rank = 0;
  }

  public Long getDatastoreId() {
    return datastoreId;
  }

  public University getUniversity() {
    return university;
  }

  public String getEventTitle() {
    return eventTitle;
  }

  public String getEventDateTime() {
    return eventDateTime;
  }
  public double getEventLatitude() {
    return eventLatitude;
  }
  public double getEventLongitude() {
    return eventLongitude;
  }
  public List<Review> getReviews() {
    return reviews;
  }

  public Long getOrganizationId() {
    return organizationId;
  }

  public String getEventDescription() {
    return eventDescription;
  }

  public String getEventType() {
    return eventType;
  }

  public String getEnergyLevel() {
    return energyLevel;
  }

  public String getLocation() {
    return location;
  }

  public Boolean getFoodAvailable() {
    return foodAvailable;
  }

  public Boolean getFree() {
    return free;
  }

  public Boolean getVisitorAllowed() {
    return visitorAllowed;
  }

  public Integer getRank() {
    return rank;
  }

  public void setDatastoreId(Long datastoreId) {
    this.datastoreId = datastoreId;
  }
  public void setUniversity(University university) {
    this.university = university;
  }
  public void setEventTitle(String eventTitle) {
    this.eventTitle = eventTitle;
  }
  public void setEventDateTime(String eventDateTime) {
    this.eventDateTime = eventDateTime;
  }
  public void setEventDescription(String eventDescription) {
    this.eventDescription = eventDescription;
  }
  public void setEventLatitude(double eventLatitude) {
    this.eventLatitude = eventLatitude;
  }
  public void setEventLongitude(double eventLongitude) {
    this.eventLongitude = eventLongitude;
  }
  public void setEventType(String eventType) {
    this.eventType = eventType;
  }
  public void setEnergyLevel(String energyLevel) {
    this.energyLevel = energyLevel;
  }
  public void setLocation(String location) {
    this.location = location;
  }
  public void setFoodAvailable(Boolean foodAvailable) {
    this.foodAvailable = foodAvailable;
  }
  public void setFree(Boolean free) {
    this.free = free;
  }
  public void setVisitorAllowed(Boolean visitorAllowed) {
    this.visitorAllowed = visitorAllowed;
  }
  public void setOrganizationId(Long organizationId) {
    this.organizationId = organizationId;
  }

  /**
   * Add new review to list
   * @param review review object
   */
  public void addReview(Review review) {
    reviews.add(review);
  }

  /**
   * Remove review from list
   * @param review review object
   */
  public void removeReview(Review review) {
    reviews.remove(review);
  }

  /**
   * increases rank by 1
   */
  public void incrementRank() {
    rank++;
  }

  /**
   * decreases rank by 1
   */
  public void decrementRank() {
    rank--;
  }

  /**
   * returns event information in a string format
   * @return String
   */
  public String toString() {
    return eventTitle + " " + datastoreId + " " + rank;
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
    if (!(o instanceof Event)) {
      return false;
    }

    Event e = (Event) o;
    return this.datastoreId.equals(e.datastoreId);
  }

  @Override
  public int hashCode() {
    return datastoreId.hashCode();
  }

  @Override
  public int compareTo(Object o) {
    if (o == this) {
      return 0;
    }
    if (!(o instanceof Event)) {
      return -1;
    }
    Event other = (Event) o;
    if (this.getEventTitle() == null || other.getEventTitle() == null) {
      return -1;
    }
    return this.getEventTitle().toLowerCase().compareTo(other.getEventTitle().toLowerCase());
  }
}
