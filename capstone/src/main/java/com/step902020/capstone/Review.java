package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;

/**
 * 
 */
@Entity(name = "review")
public class Review {
  @Id
  Long datastoreId;

  long timestamp;

  @Field(name="name")
  String name;

  @Field(name="text")
  String text;

  /**
   * Create a new Review
   * @param text Text content of review
   * @param name Review's author
   */
  public Review(String text, String name) {
    this.timestamp = System.currentTimeMillis();
    this.text = text;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getText() {
    return text;
  }
}