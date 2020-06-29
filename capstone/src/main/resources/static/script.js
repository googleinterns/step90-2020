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
  fetch('/list-events').then(response => response.json()).then((events) => {

    const eventListElement = document.getElementById('events');
    eventListElement.innerText = "Events";
    
    events.forEach((event) => {
      eventListElement.appendChild(createEventElement(event));
    }) 
  });
}

/** Creates an event element. */
function createEventElement(event) {
  const eventElement = document.createElement('li');
  eventElement.className = 'event';

  // Name 
  const nameElement = document.createElement('p');
  nameElement.innerText = event.text;

  /*
  Time
  Location
  Organization
  Description
  */
  eventElement.appendChild(nameElement);
  
  return eventElement;
}

async function newEvent() {
  await fetch('/new-event', {method: 'POST'});
}

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
