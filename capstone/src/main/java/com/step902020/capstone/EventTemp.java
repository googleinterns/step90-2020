package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import java.util.*;

@Entity(name = "eventTemp")
public class EventTemp {

  @Id
  Long datastoreId;

  String name;

  List<Review> reviews;

  public EventTemp() {
  }
  
  public EventTemp(String name) {
    this.name = name;
    reviews = new ArrayList();
  }
  
  public EventTemp(long datastoreId) {
      this.datastoreId = datastoreId;
  }

  public Long getDatastoreId() {
    return datastoreId;
  }

  public String getName() {
    return name;
  }

  public List<Review> getReviews() {
    return reviews;
  }

  public void addReview(Review review) {
    reviews.add(review);
  }
}