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

/**
 * Retrieves events from server
 */
function getEvents() {
  fetch('get-all-events').then(response => response.json()).then((events) => {

    const eventListElement = document.getElementById('events');
    eventListElement.innerText = "Events";
    
    events.forEach((event) => {
      eventListElement.appendChild(createEventElement(event));
    }) 
  });
}

/**
 * Format event listing
 */
function createEventElement(event) {
  const eventElement = document.createElement('li');
  eventElement.className = 'event';
  // Name 
  const nameElement = document.createElement('p');
  nameElement.innerText = event.name;

  const idElement = document.createElement('p');
  idElement.innerText = event.datastoreId;
  idElement.style.display = 'none';

  /*
  Time
  Location
  Organization
  Description
  */
  eventElement.appendChild(idElement);
  eventElement.appendChild(nameElement);
  eventElement.appendChild(createReviewElement(event));
  
  return eventElement;
}

/**
 * Format review element and listing
 */
function createReviewElement(event) {
  const reviewElement = document.createElement('span');

  // Submission
  const reviewInputElement = document.createElement('input');
  reviewInputElement.setAttribute('placeholder', 'Leave a review');
  reviewInputElement.setAttribute('type', 'text');

  // Future: option to add image

  const reviewButtonElement = document.createElement('button');
  reviewButtonElement.innerText = 'Submit Review';
  reviewButtonElement.addEventListener('click', () => {
    newReview(event.datastoreId, reviewInputElement.value);
  });

  // Container
  const reviewsContainer = document.createElement('div');
  reviewsContainer.innerText = 'Reviews:'
  const reviews = event.reviews;

  reviews.forEach((review) => {
      const reviewContainer = document.createElement('div');
      reviewContainer.className = 'review';
      const reviewTextElement = document.createElement('p');
      reviewTextElement.innerText = review.text;
      reviewTextElement.className = 'review-text'

      const reviewUserElement = document.createElement('p');
      reviewUserElement.innerText = review.name;
      reviewUserElement.className = 'review-name';

      reviewContainer.appendChild(reviewUserElement);
      reviewContainer.appendChild(reviewTextElement);

      reviewsContainer.appendChild(reviewContainer);
    }) 

  reviewElement.appendChild(reviewInputElement);
  reviewElement.appendChild(reviewButtonElement);
  reviewElement.appendChild(reviewsContainer);

  return reviewElement;
}

/**
 * Add review to event's list
 */
async function newReview(eventId, text) {
  var email = getEmail();
  
  const response = await fetch('get-individual?email=' + email);
  const individual = await response.json();

  const params = new URLSearchParams();
  params.append('text', text);
  params.append('eventId', eventId);
  params.append('name', individual[0].firstName + ' ' + individual[0].lastName);
  
  await fetch('new-review', {method:'POST', body: params});
  getEvents();
}

/** TEMP */
async function newEvent() {
  await fetch('save-event', {method: 'POST'});
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



