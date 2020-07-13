// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

var reviewsExist = false;
/**
 * Retrieves events from server
 */
function getEvents() {
  fetch('get-all-events').then(response => response.json()).then((events) => {

    const eventListElement = document.getElementById('events');

    events.forEach((event) => {
      eventListElement.appendChild(createEventElement(event));
    })
    // Format time to *** time ago
    if (reviewsExist){
      timeago.render(document.querySelectorAll('.timeago'));
    }
  });
}

/**
 * Format event listing
 * @param event Event from Get call
 * @return Formatted event ready to add to document
 */
function createEventElement(event) {
  const eventElement = document.createElement('li');
  eventElement.className = 'event';

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
  eventElement.appendChild(eventLocationElement);
  //eventElement.appendChild(eventOrgElement);
  eventElement.appendChild(createSaveEventButton(event));
  eventElement.appendChild(createReviewElement(event));
  
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
  reviewInputElement.setAttribute('placeholder', 'Leave a review');
  reviewInputElement.setAttribute('type', 'text');

  const reviewButtonElement = document.createElement('button');
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
  reviewsContainer.innerText = 'Reviews:'

  reviews.forEach((review) => {
  reviewsExist = true;
    const reviewContainer = document.createElement('div');
    reviewContainer.className = 'review';

    const reviewDetailsElement = document.createElement('div');
    reviewDetailsElement.className = 'review-details';
    reviewDetailsElement.innerText = review.name;

    const reviewTimeElement = document.createElement('time');
    reviewTimeElement.className = "timeago";
    reviewTimeElement.setAttribute('datetime', review.timestamp);
     
    const reviewTextElement = document.createElement('p');
    reviewTextElement.className = 'review-text';
    reviewTextElement.innerText = review.text;

    reviewDetailsElement.appendChild(document.createElement('br'));
    reviewDetailsElement.appendChild(reviewTimeElement);
    reviewContainer.appendChild(reviewDetailsElement);
    reviewContainer.appendChild(reviewTextElement);
    reviewsContainer.appendChild(reviewContainer);
  })   
  return reviewsContainer;
}

/**
 * Create new review to add to event's list
 * @paaram eventId Event's datastoreId
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

/**
 * Toggle advanced filters
 */
function showMore() {
  const filterElement = document.getElementById('additionalFilters');  
  const button = document.getElementById('filterButton');

  if (filterElement.style.display == 'block') {
    filterElement.style.display = 'none';
    button.innerText = 'Show more';
  } else {
    filterElement.style.display = 'block';
    button.innerText = 'Show less';
  }
}

function createMap() {
  var nycLatLng = {lat: 40.730610, lng: -73.935242};
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: nycLatLng, zoom: 11});
}

