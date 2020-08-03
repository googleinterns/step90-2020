/* Function to prefill event information if editing event */
function loadEventInfo() {
  const event = window.location.hash.substring(1);
  if (event != "") {
    fetch('get-event?event-id=' + event).then(response => response.json()).then((data) => {
      document.getElementById("eventTitle").value = data.eventTitle;
      document.getElementById("eventDateTime").value = data.eventDateTime;
      document.getElementById("eventLatitude").value = data.eventLatitude;
      document.getElementById("eventLongitude").value = data.eventLongitude;
      document.getElementById("eventDescription").value = data.description;
      document.getElementById("event-id").value = data.datastoreId;
      if (data.foodAvailable == true) {
        document.getElementById("foodAvailable").checked = true;
      }
      if (data.requiredFee == true) {
        document.getElementById("requiredFee").checked = true;
      }
    });
  }
}


/**
 * Retrieves events from server if current user has a profile
 */
function getEvents() {
  showSpinner();
  fetch('user-info').then(response => response.json()).then((data) => {
    if (data.userType != "unknown") {
      loadEvents(data);
    } else { // no profile
      displayMain(false);
    }
    hideSpinner();
  }).catch((error) => {
    // log error
    hideSpinner();
  });
}

/**
 * Loads events if user has a profile
 * @param Current user data
 */
function loadEvents(data) {
  var isIndividual = data.userType == "individual";

   fetch('get-filtered-events?foodAvailable=' + selectedFilter('food') + '&requiredFee=' + selectedFilter('free')).then(response => response.json()).then((events) => {
    const eventListElement = setElementInnerText('events', ''); // Clear elements in div
    events.forEach((event) => {
      createEventElement(eventListElement, event, isIndividual, false, data.email);
    })
  });
}

/**
 * Check if a filter has been selected
 * @param elementId Id of filter element
 * @return true if filter was selected, false if not
 */
function selectedFilter(elementId){
  if (document.getElementById(elementId).classList.contains('selected')) {
    return true;
  }
  return false;
}
/**
 * Create a formatted list of events
 * @param eventListElement DOM element to append
 * @param event event object
 * @param isIndividual if user is an individual user
 * @param userEvent if individual has saved the event or org owns the event
 * @param userEmail current user's email
 */
async function createEventElement(eventListElement, event, isIndividual, userEvent, userEmail) {
  const eventElement = createElement(eventListElement, 'li', '');
  eventElement.className = 'event';
  // Click for event detail modal
  eventElement.addEventListener('click', () => {
    showEventPage(event, isIndividual, userEmail);
  });

  // Name
  createElement(eventElement, 'p', event.eventTitle);

  // Time
  var date = new Date(event.eventDateTime);
  createElement(eventElement, 'p', date.toString().substring(0, 21)); // Exclude GMT time zone offset

  // Organization
  var organizationInfo = await fetch("get-public-profile?organization-id=" + event.organizationId);
  var organization = await organizationInfo.json();
  createElement(eventElement, 'p', organization.name);

  // rank
  createElement(eventElement, 'p', "Number of people following this event: " + event.rank);

  // Only for individual users can save/unsave events
  // Only for individual users can save/unsave events
  if (isIndividual) {
    if (userEvent) { // Individual has saved event
      createUnsaveEventButton(eventElement, event);
    } else {
      createSaveEventButton(eventElement, event);
    }
  } else {
    if (userEvent) {
      createEditAndDeleteEventButton(eventElement, event);
    }
  }
  return eventElement;
}

/**
 * Create a page to view event details
 * @param event event object
 * @param isIndividual if current user is an individual user
 * @param userEmail current user's email
 */
function showEventPage(event, isIndividual, userEmail) {
  fillEventDetails(event);
  const modal = document.getElementById('modal');
  modal.style.display = 'block';

  const reviewContainer = document.getElementById("review-container");
  reviewContainer.innerHTML = '';
  createReviewElement(event, isIndividual, userEmail);

  if (event.reviews.length) { // Format time to *** time ago
    timeago.render(document.querySelectorAll('.timeago'));
  }
}

/**
 * Populate event details in the modal
 * @param event event object
 */
function fillEventDetails(event) {
  fetch("get-public-profile?organization-id=" + event.organizationId).then(response => response.json()).then((data) => {

    var date = new Date(event.eventDateTime);

    setElementInnerText("eventName", event.eventTitle);
    setElementInnerText("eventTime", date.toString().substring(0, 21)); // Exclude GMT time zone offset
    setElementInnerText("eventOrganization", data.name);
    setElementInnerText("eventDescription", event.eventDescription);
    setElementInnerText("eventRank", "There are " + event.rank + " users following this event");
    createMapForASingleEvent(event);
  });
}

/**
 * Closes modal if user clicks outside of it a page to view event details
 */
window.onclick = function(event) {
  const modal = document.getElementById('modal');
  if (event.target == modal) {
    modal.style.display = "none";
  }
}

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

/* Function to prefill event information if editing event */
function loadEventInfo() {
  var searchParams = new URLSearchParams(location.search);
  var event = searchParams.get("event-id");
  if (event != null) {
    fetch('get-event?event-id=' + event).then(response => response.json()).then((data) => {
      document.getElementById("eventTitle").value = data.eventTitle;
      document.getElementById("eventDateTime").value = data.eventDateTime;
      document.getElementById("eventLatitude").value = data.eventLatitude;
      document.getElementById("eventLongitude").value = data.eventLongitude;
      document.getElementById("eventDescription").value = data.eventDescription;
      document.getElementById("event-id").value = data.datastoreId;
      if (data.foodAvailable == true) {
        document.getElementById("foodAvailable").checked = true;
      }
      if (data.requiredFee == true) {
        document.getElementById("requiredFee").checked = true;
      }
      hideSpinner();
    }).catch((error) => {
      // log error
      hideSpinner();
    });
  } else {
    hideSpinner();
  }
}

/**
 * Create a page to view event details
 * @param eventId event's datastore id
 */
function showEventPage(event) {
  fillDetails(event);
  const modal = document.getElementById('modal');
  modal.style.display = "block";
  const myNode = document.getElementById("review-container");
  myNode.innerHTML = '';
  createReviewElement(event);

  if (event.reviews.length) { // Format time to *** time ago
    timeago.render(document.querySelectorAll('.timeago'));
  }
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
  const modal = document.getElementById('modal');
  if (event.target == modal) {
    modal.style.display = "none";
  }
}
