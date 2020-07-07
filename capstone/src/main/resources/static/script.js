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

/** checks whether the user is authenticated and adjust elements 
according to whether the user is logged in or logged out */
function checkAuth(){
  // send request for information on login status
  // if request not working, default to preset value
  var email = getEmail();

  // prefill the email in the form so that user cannot edit their email
  document.getElementById("email-form-display").innerText = email;
  document.getElementById("org-email-form-display").innerText = email;
  document.getElementById("email-form").value = email;
  document.getElementById("org-email-form").value = email;
  return email;
}

function getEmail() {
  var email = "";
  fetch('_gcp_iap/identity').then(response => response.json()).then((data) => {
    email = data["email"].substring(20);
  });
  
  // Temp Back up
  if (email == "") {
      email = "jenny@google.com";
  }

  return email;
}

/* gets the user information from Datastore and display them in profile */
function getUser() {
    var email = checkAuth();

    /* Since there is no way to know beforehand whether the user is an organization 
    or an individual, we have to do two fetches to check the organization entities and
    the user entities */
    fetch('get-organization?email=' + email).then(response => response.json()).then((data) => {
      if (data.length != 0) {
          createProfile(data, true);
          displayForm(data[0].userType);
          document.getElementById("profile-section").style.display = "block";
          document.getElementById("no-profile").style.display = "none";
      } else {
          fetch('get-individual?email=' + email).then(response => response.json()).then((newData) => {
            if (newData.length != 0) {
                createProfile(newData, false);
                displayForm(newData[0].userType);
                document.getElementById("profile-section").style.display = "block";
                document.getElementById("no-profile").style.display = "none";
            } else {
                // user does not exist at all, prompt them to submit a profile
                document.getElementById("profile-section").style.display = "none";
                document.getElementById("no-profile").style.display = "block";
            }
          });  
      }
  });
}

/* creates and populates the user profile */
function createProfile(data, isOrganization) {
    const emailFormContainer = document.getElementById("email-form");
    emailFormContainer.value = data[0].email;
    document.getElementById("email-form-display").innerText = data[0].email;

    const idFormContainer = document.getElementById("datastore-id");
    idFormContainer.value = data[0].datastoreId;

    const emailContainer = document.getElementById("email");
    const pElementEmail = document.createElement('p');
    pElementEmail.innerText = "Email: " + data[0].email;
    emailContainer.appendChild(pElementEmail);

    const userTypeContainer = document.getElementById("user-type");
    const pElementUserType = document.createElement('p');
    pElementUserType.innerText = "User Type: " + data[0].userType;
    userTypeContainer.appendChild(pElementUserType);

    const universityContainer = document.getElementById("university");
    const pElementUniversity = document.createElement('p');
    pElementUniversity.innerText = "University: " + data[0].university;
    universityContainer.appendChild(pElementUniversity);

    if (isOrganization) {
      createOrgProfile(data);
    } else {
      createIndividualProfile(data);
    }

}

/* populate individual specific fields of the profile */
function createIndividualProfile(data) {
  const firstNameContainer = document.getElementById("firstname");
  const pElementFirstName = document.createElement('p');
  pElementFirstName.innerText = "First Name: " + data[0].firstName;
  firstNameContainer.appendChild(pElementFirstName);

  const lastNameContainer = document.getElementById("lastname");
  const pElementLastName = document.createElement('p');
  pElementLastName.innerText = "Last Name: " + data[0].lastName;
  lastNameContainer.appendChild(pElementLastName);

  document.getElementById("org-name").style.display = "none";
  document.getElementById("description").style.display = "none";
}

/* populate organization specific fields of the profile */
function createOrgProfile(data) {
  const nameContainer = document.getElementById("org-name");
  const pElementName = document.createElement('p');
  pElementName.innerText = "Organization Name: " + data[0].name;
  nameContainer.appendChild(pElementName);

  const descriptionContainer = document.getElementById("description");
  const pElementDescription = document.createElement('p');
  pElementDescription.innerText = "Description: " + data[0].description;
  descriptionContainer.appendChild(pElementDescription);

  document.getElementById("firstname").style.display = "none";
  document.getElementById("lastname").style.display = "none";
}

// function to toggle between the two different forms
function displayForm(userType) {
  if (userType == "individual") {
    document.getElementById("user").style.display = "block";
    document.getElementById("organization").style.display = "none";
    document.getElementById("user-type-toggle").value = "individual";

  } else if (userType == "organization") {
    document.getElementById("user").style.display = "none";
    document.getElementById("organization").style.display = "block";
    document.getElementById("org-user-type").value = "organization";
  }
}

function createDivElement(event) {
  const divElement = document.createElement('div');
  divElement.setAttribute("class", "item-container");
 
  const h3ElementName = document.createElement('h3');
  h3ElementName.innerText = event;
  divElement.appendChild(h3ElementName);

}

// function used to toggle after a change in the selected user type input
function toggleForm(formUserType) {
  var userType = document.getElementById(formUserType).value;
  displayForm(userType);
}
