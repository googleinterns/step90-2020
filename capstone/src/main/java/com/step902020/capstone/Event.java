package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Reference;
import org.springframework.data.annotation.Id;

import java.util.*;

@Entity(name = "event")
public class Event{

  @Id
  Long datastoreId;

  @Reference
  University university;

  String organizationName;

  Long organizationId;

  @Field(name="eventTitle")
  String eventTitle;

  @Field(name="eventDateTime")
  String eventDateTime;

  @Field(name="eventDescription")
  String eventDescription;

  @Field(name="eventLatitude")
  double eventLatitude;

  @Field(name="eventLongitude")
  double eventLongitude;

  @Field(name="foodAvailable")
  Boolean foodAvailable;

  @Field(name="requiredFee")
  Boolean requiredFee;
  
  @Reference
  List<Review> reviews;

  Integer rank;

  public Event(University university, String organizationName, long organizationId, String eventTitle,String eventDateTime, String eventDescription, double eventLatitude, double eventLongitude, Boolean foodAvailable, Boolean requiredFee) {
    this.university = university;
    this.organizationName = organizationName;
    this.organizationId = organizationId;
    this.eventTitle = eventTitle;
    this.eventDateTime = eventDateTime;
    this.eventDescription = eventDescription;
    this.eventLatitude = eventLatitude;
    this.eventLongitude = eventLongitude;
    this.foodAvailable = foodAvailable;
    this.requiredFee = requiredFee;
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

  public String getOrganizationName() {
    return organizationName;
  }

  public Long getOrganizationId() {
    return organizationId;
  }

  public String getEventDescription() {
    return eventDescription;
  }

  public Boolean getFoodAvailable() {
    return foodAvailable;
  }

  public Boolean getRequiredFee() {
    return requiredFee;
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
  public void setFoodAvailable(Boolean foodAvailable) {
    this.foodAvailable = foodAvailable;
  }
  public void setRequiredFee(Boolean requiredFee) {
    this.requiredFee = requiredFee;
  }

  public void setOrganizationName(String organizationName) {
    this.organizationName = organizationName;
  }

  public void setOrganizationId(Long organizationId) {
    this.organizationId = organizationId;
  }

  /**
   * Add new review to list
   * @param review Review object
   */
  public void addReview(Review review) {
    reviews.add(review);
  }

  /**
   * increases rank by 1
   */
  public void addRank() {
    rank++;
  }

  /**
   * decreases rank by 1
   */
  public void minusRank() {
    rank--;
  }

  /**
   * returns event information in a string format
   * @return String
   */
  public String toString() {
    return eventTitle + " " + datastoreId;
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

}
