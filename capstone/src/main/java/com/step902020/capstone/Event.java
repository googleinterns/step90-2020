package com.step902020.capstone;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Reference;
import org.springframework.data.annotation.Id;
import java.util.*;

@Entity(name = "event")
public class Event{

  @Id
  Long datastoreID;

  @JsonBackReference
  Organization organization;

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

  @Field(name="foodAvaliable")
  Boolean foodAvaliable;

  @Field(name="requiredFee")
  Boolean requiredFee;
  
  @Reference
  List<Review> reviews;
 

  public Event(Organization organization, String eventTitle,String eventDateTime, String eventDescription, double eventLatitude, double eventLongitude, Boolean foodAvaliable, Boolean requiredFee) {
 
    this.organization = organization;
    this.eventTitle = eventTitle;
    this.eventDateTime = eventDateTime;
    this.eventDescription = eventDescription;
    this.eventLatitude = eventLatitude;
    this.eventLongitude = eventLongitude;
    this.foodAvaliable = foodAvaliable == null ? false : foodAvaliable;
    this.requiredFee = requiredFee == null ? false : requiredFee;
    this.reviews = new ArrayList();
  }

  public Long getDatastoreID() {
    return datastoreID;
  }
  public String getEventTitle() {
    return eventTitle;
  }
  public Organization getOrganization() {
    return organization;
  }
  public String getEventDateTime() {
    return eventDateTime;
  }
  public String getDescription() {
    return eventDescription;
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

  public void setDatastoreID(Long datastoreID) {
    this.datastoreID = datastoreID;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
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
  public void setFoodAvaliable(Boolean foodAvaliable) {
    this.foodAvaliable = foodAvaliable;
  }
  public void setRequiredFee(Boolean requiredFee) {
    this.requiredFee = requiredFee;
  }

  /**
   * Add new review to list
   * @param review Review object
   */
  public void addReview(Review review) {
    reviews.add(review);
  }
}
