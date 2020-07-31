/**
 * Create event's review submission elements and formats review listing
 * Only individuals will see review submission option
 * @param event event object
 * @param isIndividual if user is an individual user
 * @param userEmail current user's email
 */
function createReviewElement(event, isIndividual, userEmail) {
  const reviewElement = document.getElementById('review-container');
  const reviewTitleElement = createElement(reviewElement, 'h1', 'Reviews');

  if (isIndividual) {
    const reviewInputElement = createElement(reviewElement, 'input', '');
    reviewInputElement.className = 'review-submission';
    reviewInputElement.setAttribute('placeholder', 'Leave a review');
    reviewInputElement.setAttribute('type', 'text');

    reviewButtonElement = createElement(reviewElement, 'button', 'Submit');
    reviewButtonElement.className = 'review-submission';

    reviewButtonElement.addEventListener('click', () => {
      if (reviewInputElement.value != '') {
        newReview(event.datastoreId, reviewInputElement.value).then((reviews) => {
          reviewsContainer.innerHTML = '';
          createReviewContainerElement(reviewsContainer, reviews, userEmail);
        });
      }
    });
  }
  const reviewsContainer = createElement(reviewElement, 'div', '');
  reviewsContainer.id = 'review-list-container';
  createReviewContainerElement(reviewsContainer, event.reviews, userEmail);
}

/**
 * Formats each review to add to review container
 * Users can like each review once
 * Individuals can edit/delete their reviews
 * @param reviewsContainer container for event's review list
 * @param reviews event's reviews
 * @param userEmail current user's email

 */
function createReviewContainerElement(reviewsContainer, reviews, userEmail) {
  reviews.forEach((review) => {
    const reviewContainer = createElement(reviewsContainer, 'div', '');
    reviewContainer.className = 'review';

    const reviewDetailsElement = createElement(reviewContainer, 'div', '');
    reviewDetailsElement.className = 'review-details';

    createElement(reviewDetailsElement, 'p', review.individualName);

    const reviewTimeElement = createElement(reviewDetailsElement, 'time', '');
    reviewTimeElement.className = 'timeago';
    reviewTimeElement.setAttribute('datetime', review.timestamp);

    const reviewTextElement = createElement(reviewContainer, 'p', review.text);
    reviewTextElement.className = 'review-text';

    const reviewLikeElement = createElement(reviewContainer, 'button',  review.likes + ' Likes');
    reviewLikeElement.addEventListener('click', () => {
      toggleReviewLike(review.datastoreId).then((reviewLikes) => {
        reviewLikeElement.innerText = reviewLikes + ' Likes';
      });
    });

    if (review.individualEmail == userEmail) {
      createReviewEditButton(reviewContainer, reviewTextElement, review.datastoreId);
      createReviewDeleteButton(reviewContainer, review.datastoreId);
    }
  })
}

/**
 * Add delete functionality for review's author
 * @param reviewContainer review's container
 * @param reviewId review's datastore id
 */
function createReviewDeleteButton(reviewContainer, reviewId) {
  const deleteButton = createElement(reviewContainer, 'button', 'Delete');
  deleteButton.addEventListener('click', () => {
    deleteReview(reviewId);
    reviewContainer.remove();
  });
}

/**
 * Add edit functionality for review's author
 * @param reviewContainer review's container
 * @param reviewTextElement container for review's text
 * @param reviewId review's datastore id
 */
function createReviewEditButton(reviewContainer, reviewTextElement, reviewId) {
  const editButton = createElement(reviewContainer, 'button', 'Edit');
  editButton.addEventListener('click', () => {
    if (editButton.innerText == 'Edit') {
      reviewTextElement.contentEditable = true;
      reviewTextElement.focus();
      editButton.innerText = 'Done';
    } else {
      setReviewText(reviewId, reviewTextElement.innerText);
      reviewTextElement.contentEditable = false;
      editButton.innerText = 'Edit';
    }
  });
}

/**
 * Create new review to add to event's list
 * @param eventId event's datastoreId
 * @param text review's text content
 * @return update list of reviews
 */
async function newReview(eventId, text) {
  const params = new URLSearchParams();
  params.append('text', text);
  params.append('eventId', eventId);
  const response = await fetch('new-review', {method:'POST', body: params});
  const reviews = response.json();
  getEvents();
  return reviews;
}

/**
 * Remove a review from event's list
 * @param reviewId review's datastoreId
 */
function deleteReview(reviewId) {
  const params = new URLSearchParams();
  params.append('reviewId', reviewId);
  fetch('delete-review', {method:'POST', body: params});
  getEvents();
}

/**
 * Toggle like to review's like count
 * Individuals can only like once
 * @param reviewId review's datastoreId
 * @return updated like count
 */
async function toggleReviewLike(reviewId) {
  const params = new URLSearchParams();
  params.append('reviewId', reviewId);
  const response = await fetch('toggle-likes', {method:'POST', body: params});
  const value = response.json();
  getEvents();
  return value;
}

/**
 * Set review's text to author's entered text
 * @param reviewId review's datastore id
 * @param newText text to replace prev review's text
 */
function setReviewText(reviewId, newText) {
  const params = new URLSearchParams();
  params.append('newText', newText);
  params.append('reviewId', reviewId);
  fetch('set-text', {method:'POST', body: params});
  getEvents();
}