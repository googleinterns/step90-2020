/**
 * Retrieves events from server
 */
function getEvents() {
  var userType = sessionStorage.getItem("user-type");
  if (userType == null) {
    getUserType(false);
    if (sessionStorage.getItem("user-type") == null) {
      return;
    }
  }
  var displaySaveButton = userType == "individual";
  fetch('get-all-events').then(response => response.json()).then((events) => {

    const eventListElement = document.getElementById('events');
    eventListElement.innerText = ""; // Clear elements in div

    events.forEach((event) => {
      eventListElement.appendChild(createEventElement(event, displaySaveButton));
    })
  });
}

/**
 * Format event listing
 * @param event Event from Get call
 * @return Formatted event ready to add to document
 */
function createEventElement(event, displaySaveButton) {
  const eventElement = document.createElement('li');
  eventElement.className = 'event';

  // Click for event detail modal
  eventElement.addEventListener('click', () => {
    showEventPage(event);
  });

  // Name
  const eventNameElement = document.createElement('p');
  eventNameElement.innerText = event.eventTitle;

  // Time
  var date = new Date(event.eventDateTime);
  const eventTimeElement = document.createElement('p');
  eventTimeElement.innerText = date.toString().substring(0, 21); // Exclude GMT time zone offset

  // Location Using latitude as a filler until we finalize the location portion
   const eventLocationElement = document.createElement('p');
   eventLocationElement.innerText = event.eventLatitude;

  // Organization
  //const eventOrgElement = document.createElement('p');
  //eventOrgElement.innerText = event.organization.name;

  eventElement.appendChild(eventNameElement);
  eventElement.appendChild(eventTimeElement);
  //eventElement.appendChild(eventOrgElement);
  if (displaySaveButton) {
    eventElement.appendChild(createSaveEventButton(event));
  }
  return eventElement;
}

/**
 * Create event's review submission and format review listing
 * @param event Event from Get call
 * @return review section of event's listing
 */
function createReviewElement(event) {
  const reviewElement = document.createElement('div');
  reviewElement.className = 'reviews';

  const reviewInputElement = document.createElement('input');
  reviewInputElement.className = 'review-submission';
  reviewInputElement.setAttribute('placeholder', 'Leave a review');
  reviewInputElement.setAttribute('type', 'text');

  reviewButtonElement = document.createElement('button');
  reviewButtonElement.className = 'review-submission';
  reviewButtonElement.innerText = 'Submit Review';
  reviewButtonElement.addEventListener('click', () => {
    if (reviewInputElement.value != '') {
      newReview(event.datastoreID, reviewInputElement.value);
    }
  });

  reviewElement.appendChild(reviewInputElement);
  reviewElement.appendChild(reviewButtonElement);
  reviewElement.appendChild(createReviewContainerElement(event.reviews));
  return reviewElement;
}

/**
 * Format each review to add to review container
 * @param reviews List of reviews within Event object
 * @return List of event's reviews to add to document
 */
function createReviewContainerElement(reviews) {
  const reviewsContainer = document.createElement('div');

  reviews.forEach((review) => {
    const reviewContainer = document.createElement('div');
    reviewContainer.className = 'review';

    const reviewDetailsElement = document.createElement('div');
    reviewDetailsElement.className = 'review-details';

    const reviewNameElement = document.createElement('p');
    reviewNameElement.innerText = review.name;

    const reviewTimeElement = document.createElement('time');
    reviewTimeElement.className = "timeago";
    reviewTimeElement.setAttribute('datetime', review.timestamp);

    const reviewTextElement = document.createElement('p');
    reviewTextElement.className = 'review-text';
    reviewTextElement.innerText = review.text;

    reviewDetailsElement.appendChild(reviewNameElement);
    reviewDetailsElement.appendChild(reviewTimeElement);
    reviewContainer.appendChild(reviewDetailsElement);
    reviewContainer.appendChild(reviewTextElement);
    reviewsContainer.appendChild(reviewContainer);
  })
  return reviewsContainer;
}

/**
 * Create new review to add to event's list
 * @param eventId Event's datastoreId
 * @param text Text content of Review
 */
async function newReview(eventId, text) {
  const params = new URLSearchParams();
  params.append('text', text);
  params.append('eventId', eventId);
  //params.append('name', individual[0].firstName + ' ' + individual[0].lastName);
  //Quick fix until create a new way to attach user to review
  params.append('name', 'quick-fix');
  await fetch('new-review', {method:'POST', body: params});
  getEvents();
}

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
      document.getElementById("event-id").value = data.datastoreID;
    });
  }
}

/**
 * Create a page to view event details
 * @param eventId event's datastore id
 */
function fillDetails(event) {
  const eventName = document.getElementById("eventName");
  eventName.innerText = event.eventTitle;

  var date = new Date(event.eventDateTime);
  const eventTime = document.getElementById("eventTime");
  eventTime.innerText = date.toString().substring(0, 21);

  const eventLocation = document.getElementById("eventLocation");
  eventLocation.innerText = event.eventLatitude;

  const eventDescription = document.getElementById("eventDescription");
  eventDescription.innerText = event.description;
}

/**
 * Create a page to view event details
 * @param eventId event's datastore id
 */
function showEventPage(event) {
    fillDetails(event);
    const modal = document.getElementById('modal');
    modal.style.display = "block";
    const reviewContainer = document.getElementById("reviews-container");
    reviewContainer.innerText = "Reviews:";
    console.log(event);
    reviewContainer.appendChild(createReviewElement(event));

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