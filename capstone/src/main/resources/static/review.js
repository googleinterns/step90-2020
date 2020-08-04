/**
 * Create event's review submission elements and formats review listing
 * Only individuals will see review submission option
 * @param reviewContainer container for review functions and reviews
 * @param reviewedObject event or organization object
 * @param isIndividual if user is an individual user
 * @param userEmail current user's email
 */
function createReviewElement(reviewContainer, reviewedObject, isIndividual, userEmail) {
  const reviewTitleElement = createElement(reviewContainer, 'h1', 'Reviews');
console.log("hey");
  if (isIndividual) {
    const reviewInputElement = createElement(reviewContainer, 'input', '');
    reviewInputElement.className = 'review-submission';
    reviewInputElement.setAttribute('placeholder', 'Leave a review');
    reviewInputElement.setAttribute('type', 'text');

    reviewButtonElement = createElement(reviewContainer, 'button', 'Submit');
    reviewButtonElement.className = 'review-submission';

    reviewButtonElement.addEventListener('click', () => {
      if (reviewInputElement.value != '') {
        newReview(reviewContainer.id, reviewedObject.datastoreId, reviewInputElement.value).then((reviewedObject) => {
          reviewElementsContainer.innerHTML = '';
          createReviewContainerElement(reviewElementsContainer, reviewedObject, userEmail);
        });
      }
    });
  }
  const reviewElementsContainer = createElement(reviewContainer, 'div', '');
  reviewElementsContainer.id = 'review-list-container';
  createReviewContainerElement(reviewElementsContainer, reviewedObject, userEmail);
}

/**
 * Formats each review to add to review container
 * Users can like each review once
 * Individuals can edit/delete their reviews
 * @param reviewsContainer container for event's review list
 * @param reviewedObject event or organization object
 * @param userEmail current user's email
 */
function createReviewContainerElement(reviewsContainer, reviewedObject, userEmail) {
  reviewedObject.reviews.forEach((review) => {
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
      createReviewDeleteButton(reviewContainer, review.datastoreId, reviewedObject.datastoreId);
    }
  })
}

/**
 * Add delete functionality for review's author
 * @param reviewContainer review's container
 * @param reviewId review's datastore id
 * @param reviewedObjectId object datastore id review is attached to
 */
function createReviewDeleteButton(reviewContainer, reviewId, reviewedObjectId) {
  const deleteButton = createElement(reviewContainer, 'button', 'Delete');
  deleteButton.addEventListener('click', () => {
    deleteReview(reviewContainer.parentElement.parentElement.id, reviewId, reviewedObjectId);
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
 * @param reviewContainerId review container's element's id
 * @param reviewedObjectId event or organization review will be attached to
 * @param text review's text content
 * @return updated reviewed object
 */
async function newReview(reviewContainerId, reviewedObjectId, text) {
  const params = new URLSearchParams();
  params.append('text', text);
  params.append('reviewedObjectId', reviewedObjectId);
  var response;
  // Event reviews
  if (reviewContainerId == 'event-review-container') {
    response = await fetch('add-event-review', {method:'POST', body: params});
    // Reload event container or page
    var windowPathName = window.location.pathname;
    if (windowPathName == '/savedevents.html') {
       getIndividualEventsOrOrganizations(true);
     } else if (windowPathName == '/publicprofile.html') {
       getPublicProfile(true);
     } else {
       getEvents();
     }
  }

  // Organization reviews (no need for reload because review will stay appended)
  if (reviewContainerId == 'org-review-container') {
    response = await fetch('add-org-review', {method:'POST', body: params});
  }
  const updatedReviewedObject = response.json();
  return updatedReviewedObject;
}

/**
 * Remove a review from event's list
 * @param reviewContainerId review container's element's id
 * @param reviewId review's datastoreId
 * @param reviewedObjectId object datastore id review is attached to
 */
function deleteReview(reviewContainerId, reviewId, reviewedObjectId) {
  const params = new URLSearchParams();
  params.append('reviewId', reviewId);
  params.append('reviewedObjectId', reviewedObjectId);
  // Event reviews
  if (reviewContainerId == 'event-review-container') {
    fetch('remove-event-review', {method:'POST', body: params});
    // Reload event container or page
    var windowPathName = window.location.pathname;
    if (windowPathName == '/savedevents.html') {
       getIndividualEventsOrOrganizations(true);
    } else if (windowPathName == '/publicprofile.html') {
       getPublicProfile(true);
    } else {
       getEvents();
    }
  }
  // Organization reviews
  if (reviewContainerId == 'org-review-container') {
    fetch('remove-org-review', {method:'POST', body: params});
    window.location.href = window.location.href;
  }
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