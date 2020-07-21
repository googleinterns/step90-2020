package com.step902020.capstone;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Review. A Review is attached to en Event
 * and contains the user who created  it, the text content, and the amt of likes it has received
 * @author lbourret
 */
@Entity(name = "review")
public class Review {
  @Id
  Long datastoreId;

  long timestamp;

  int likes;

  String individualName;

  String individualEmail;

  @Field(name="text")
  String text;

  List<String> reviewLikers;

  /**
   * Creates a new Review
   * @param individualName Name of individual who authored review
   * @param text Text content of review
   */
  public Review(String individualName, String individualEmail, String text) {
    this.timestamp = System.currentTimeMillis();
    this.likes = 0;
    this.individualName = individualName;
    this.individualEmail = individualEmail;
    this.text = text;
    this.reviewLikers = new ArrayList();
  }

  public Long getDatastoreId() {
    return datastoreId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public int getLikes() {
    return likes;
  }

  public String getIndividualName() {
    return individualName;
  }

  public String getIndividualEmail() {
    return individualEmail;
  }

  public String getText() {
    return text;
  }

  public List<String> getReviewLikers() {
    return reviewLikers;
  }

  public void setText(String text) {
    this.text = text;
  }

  /**
   * Increase review's like count by one
   */
  public void addLike() {
    likes++;
  }

  /**
   * Decrease review's like count by one
   */
  public void removeLike() {
    likes--;
  }

  /**
   * Remove individual who added their like
   * @param email Email of individual who liked review
   */
  public void addReviewLiker(String email) {
    reviewLikers.add(email);
  }

  /**
   * Remove individual who removed their like
   * @param email Email of individual who unlike review
   */
  public void removeReviewLiker(String email) {
    reviewLikers.remove(email);
  }

  /**
   * Individual has liked this review
   * @param email Email of Individual to check if they've liked review
   * @return true if individual has liked review
   */
  public boolean isReviewLiker(String email) {
    return reviewLikers.contains(email);
  }
}