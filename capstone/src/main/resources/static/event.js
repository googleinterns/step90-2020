/**
 * Retrieves events from server
 */
function getEvents() {
  fetch('user-info').then(response => response.json()).then((data) => {
    if (data.userType != "unknown") {
      getAllEventsForSearch(data);
    } else {
      displayMain(false);
    }
  });
}

/* helper function to get all events for search page */
function getAllEventsForSearch(data) {
  var displaySaveButton = data.userType == "individual";
  fetch('get-all-events').then(response => response.json()).then((events) => {

    const eventListElement = setElementInnerText('events', ''); // Clear elements in div

    events.forEach((event) => {
      createEventElement(eventListElement, event, displaySaveButton);
    })
  });
}

/**
 * Create a formatted list of events
 * @param eventListElement DOM element to append
 * @param event Event from Get call
 * @param displaySaveButton button to save event
 */
function createEventElement(eventListElement, event, displaySaveButton) {
  const eventElement = createElement(eventListElement, 'li', '');
  eventElement.className = 'event';

  // Click for event detail modal
  eventElement.addEventListener('click', () => {
    showEventPage(event);
  });

  // Name
  const eventNameElement = createElement(eventElement, 'p', event.eventTitle);

  // Time
  var date = new Date(event.eventDateTime);
  const eventTimeElement = createElement(eventElement, 'p', date.toString().substring(0, 21)); // Exclude GMT time zone offset

  // Location Using latitude as a filler until we finalize the location portion
  const eventLocationElement = createElement(eventElement, 'p', event.eventLatitude);

  // Organization
  //const eventOrgElement = document.createElement('p');
  //eventOrgElement.innerText = event.organization.name;
  //eventElement.appendChild(eventOrgElement);

  // Displays only for individual users
  if (displaySaveButton) {
    eventElement.appendChild(createSaveEventButton(event));
  }
}

/**
 * Create event's review submission elements and formats review listing
 * @param event Event from Get call
 */
function createReviewElement(event) {
  const reviewElement = document.getElementById('review-container');

  const reviewTitleElement = createElement(reviewElement, 'h1', 'Reviews');

  const reviewInputElement = createElement(reviewElement, 'input', '');
  reviewInputElement.className = 'review-submission';
  reviewInputElement.setAttribute('placeholder', 'Leave a review');
  reviewInputElement.setAttribute('type', 'text');

  reviewButtonElement = createElement(reviewElement, 'button', 'Submit');
  reviewButtonElement.className = 'review-submission';

  reviewButtonElement.addEventListener('click', () => {
    if (reviewInputElement.value != '') {
      newReview(event.datastoreID, reviewInputElement.value).then((reviews) => {
        reviewsContainer.innerHTML = '';
        createReviewContainerElement(reviewsContainer, reviews);
       });
    }
  });
  const reviewsContainer = createElement(reviewElement, 'div', '');
  reviewsContainer.id = 'review-list-container';
  createReviewContainerElement(reviewsContainer, event.reviews);
}

/**
 * Formats each review to add to review container
 * @param reviews List of reviews within Event object
 */
async function createReviewContainerElement(reviewsContainer, reviews) {
  reviews.forEach((review) => {
    const reviewContainer = createElement(reviewsContainer, 'div', '');
    reviewContainer.className = 'review';

    const reviewDetailsElement = createElement(reviewContainer, 'div', '');
    reviewDetailsElement.className = 'review-details';

    const reviewNameElement = createElement(reviewDetailsElement, 'p', review.name);

    const reviewTimeElement = createElement(reviewDetailsElement, 'time', '');
    reviewTimeElement.className = 'timeago';
    reviewTimeElement.setAttribute('datetime', review.timestamp);

    const reviewTextElement = createElement(reviewContainer, 'p', review.text);
    reviewTextElement.className = 'review-text';
  })
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
  const response = await fetch('new-review', {method:'POST', body: params});
  const reviews = response.json();
  getEvents();
  return reviews;

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
      document.getElementById("event-id").value = data.datastoreId;
    });
  }
}

function createMap() {
  var princetonLatLng = {lat: 40.3428452, lng: -74.6568153};
  const campusMap = new google.maps.Map(
    document.getElementById('map'),
    {center: princetonLatLng, zoom: 16});

  const event1 = new google.maps.Marker({
      position: {lat: 40.344184,lng: -74.657645},
      map: campusMap,
      title: "Event 1"
  });
  const event2 = new google.maps.Marker({
      position: {lat: 40.346875,lng: -74.65002},
      map: campusMap,
      title: "Event 2"
  });
  const event3 = new google.maps.Marker({
      position: {lat: 40.348357,lng: -74.660553},
      map: campusMap,
      title: "Event 3"
  });
}

function createMarker(event) {
  var eventPosition = {lat: event.eventLatitude, lng: event.eventLongitude};
  const newMarker = new google.maps.Marker({
    map: campusMap,
    title: event.eventTitle,
    position: eventPosition
  })
}
  
/**
 * Create a page to view event details
 * @param eventId event's datastore id
 */
function fillDetails(event) {
  var date = new Date(event.eventDateTime);

  setElementInnerText("eventName", event.eventTitle);
  setElementInnerText("eventTime", date.toString().substring(0, 21)); // Exclude GMT time zone offset
  setElementInnerText("eventLocation", event.eventLatitude);
  setElementInnerText("eventDescription", event.description);
}

/**
 * Fill an existing document element's inner text
 * @param elementId document element's id
 * @param innerText text for inner text
 * @return element with added text
 */
function setElementInnerText(elementId, innerText){
  const element = document.getElementById(elementId);
  element.innerText = innerText;
  return element;
}

/**
 * Create new element with inner text and appended to an element
 * @param elementElement element to append
 * @param elementType element to create
 * @param innerText text for inner text
 * @return created element
 */
function createElement(appendElement, elementType, innerText){
  const element = document.createElement(elementType);
  element.innerText = innerText;
  appendElement.appendChild(element);
  return element;
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