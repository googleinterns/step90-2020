package com.step902020.capstone;

import com.step902020.capstone.security.CurrentUser;
import java.io.IOException;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import com.google.gson.Gson;
import java.io.IOException;

/**
 * Review functionalities
 *   - Like Review
 * Author review functionalities:
 *   - Edit reviews
 *   - Delete reviews
 */
@RestController
public class ReviewController {

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private IndividualRepository individualRepository;

  /**
   * Toggle user's like
   * Add like if they have not already liked it. Remove like if they have already liked it
   * @param user current user
   * @param reviewId review's datastore id
   * @return amount of review's likes
   */
  @PostMapping("toggle-likes")
  public int toggleReviewLikes(
          CurrentUser user,
         @RequestParam("reviewId") Long reviewId) throws IOException {
    Review review = this.reviewRepository.findById(reviewId).get();
    String email = user.getEmail();
    if (review.isReviewLiker(email)) {
      review.removeLike();
      review.removeReviewLiker(email);
    } else {
      review.addLike();
      review.addReviewLiker(email);
    }
    this.reviewRepository.save(review);
    return review.likes;
  }

  /**
   * Set review's text (Only review's author can edit review)
   * @param user current user
   * @param newText text to replace prev. text content
   * @param reviewId review's datastore id
   */
  @PostMapping("set-text")
  public void setText(
          CurrentUser user,
          @RequestParam("newText") String newText,
          @RequestParam("reviewId") Long reviewId) throws IOException{

    Review review = this.reviewRepository.findById(reviewId).get();
    if (review.individualEmail.equals(user.getEmail())) {
      review.setText(newText);
      this.reviewRepository.save(review);
    }
  }
}