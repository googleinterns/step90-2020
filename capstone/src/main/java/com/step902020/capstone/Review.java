package com.step902020.capstone;

import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Review. A Review is attached to an Event
 */
@Entity(name = "review")
public class Review {
  @Id
  Long datastoreId;

  long timestamp;

  int likes;

  String individualName;

  String individualEmail;

  String text;

  List<String> reviewLikers;

  /**
   * Creates a new Review
   * @param individualName name of individual who authored review
   * @param individualEmail email of individual who authored review
   * @param text text content of review
   */
  public Review(String individualName, String individualEmail, String text) {
    this.timestamp = System.currentTimeMillis();
    this.individualName = individualName;
    this.individualEmail = individualEmail;
    this.text = text;
    this.likes = 0;
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

  public String getIndividualEmail() { return individualEmail; }

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
   * Add individual who added their like
   * @param email email of individual who liked review
   */
  public void addReviewLiker(String email) {
    reviewLikers.add(email);
  }

  /**
   * Remove individual who removed their like
   * @param email email of individual who unlike review
   */
  public void removeReviewLiker(String email) {
    reviewLikers.remove(email);
  }

  /**
   * Individual has liked this review
   * @param email email of individual to check if they've liked review
   * @return true if individual has liked review
   */
  public boolean isReviewLiker(String email) {
    return reviewLikers.contains(email);
  }
}