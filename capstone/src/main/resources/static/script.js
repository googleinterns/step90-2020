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

/* get the user information for the profile page */
function getUser(fillForm) {
  var userType = sessionStorage.getItem("user-type");

  // if there is no userType stored in this session, that means this is a new user
  if (userType == null) {
    getUserType();
  } else {
    if (userType == "organization") {
      fetch('get-organization').then(response => response.json()).then((data) => {
        createProfile(data[0], fillForm, true);
        sessionStorage.setItem("user-type", data[0].userType);
      });
    } else if (userType == "individual") {
      fetch('get-individual').then(response => response.json()).then((data) => {
        createProfile(data[0], fillForm, false);
        sessionStorage.setItem("user-type", data[0].userType);
      });  
    }
    displayMain(true);
  }
}


/* function to toggle between displaying user profile and displaying an error message */
function displayMain(display) {
  if (display) {
    document.getElementById("no-profile").style.display = "none";
  } else {
    document.getElementById("no-profile").style.display = "block";
  }
}

/* get and store the user type (individual or organization). With this we determine whether
this user exists in our database or not */
function getUserType() {
    /* Since there is no way to know beforehand whether the user is an organization 
    or an individual, we have to do two fetches to check the organization entities and
    the user entities */
    fetch('get-organization').then(response => response.json()).then((data) => {
      if (data.length != 0) {
          sessionStorage.setItem("user-type", data[0].userType);
          displayMain(true);
      } else {
        fetch('get-individual').then(response => response.json()).then((newData) => {
          if (newData.length != 0) {
            // display information
            sessionStorage.setItem("user-type", newData[0].userType);
            displayMain(true);
          } else {
            // user does not exist at all, display message to them to submit a profile
            displayMain(false);
            displayForm("individual", true);
          }
        });  
      }
  });
}

/* creates and populates the user profile */
function createProfile(data, fillForm, isOrganization) {
    const emailContainer = document.getElementById("email");
    const pElementEmail = document.createElement('p');
    pElementEmail.innerText = data.email;
    emailContainer.appendChild(pElementEmail);

    const universityContainer = document.getElementById("university");
    const pElementUniversity = document.createElement('p');
    pElementUniversity.innerText = data.university;
    universityContainer.appendChild(pElementUniversity);

    // addresses the different fields for each user type
    if (isOrganization) {
      createOrgProfile(data, fillForm);
    } else {
      createIndividualProfile(data, fillForm);
    }

}

/* populate individual specific fields of the profile */
function createIndividualProfile(data, fillForm) {
  const nameContainer = document.getElementById("name");
  const pElementName = document.createElement('h1');
  pElementName.innerText = data.firstName + " " + data.lastName;
  nameContainer.appendChild(pElementName);

  // hide fields that pertain to organizations only
  document.getElementById("description").style.display = "none";

  if (fillForm) {
    // prefill form
    document.getElementById("ind-firstname").value = data.firstName;
    document.getElementById("ind-lastname").value = data.lastName;
    document.getElementById("university-form-display").display = "block";
    document.getElementById("university-form-display").innerText = data.university;
  }
}

/* populate organization specific fields of the profile */
function createOrgProfile(data, fillForm) {
  const nameContainer = document.getElementById("name");
  const pElementName = document.createElement('h1');
  pElementName.innerText = data.name;
  nameContainer.appendChild(pElementName);

  const descriptionContainer = document.getElementById("description");
  const pElementDescription = document.createElement('p');
  pElementDescription.innerText = "About Us: " + data.description;
  descriptionContainer.appendChild(pElementDescription);

  if (fillForm) {
    // prefill form
    document.getElementById("org-form-name").value = data.name;
    document.getElementById("org-university-form-display").innerText = data.university;
    document.getElementById("org-description").value = data.description;
  }
}

// function used to toggle after a change in the selected user type input
function toggleForm(formUserType) {
  var userType = document.getElementById(formUserType).value;
  displayForm(userType, true);
}

// helper function for displaying forms
// a new user will be able to toggle, but a returning user will not
function displayForm(userType, displayBoth) {
  if (userType == "individual") {
    updateUserTypeInForm("user", "organization", "user-type-toggle", "individual");
    if (!displayBoth) {
      hideFields("user-select", "ind-uni");
    }
  } else if (userType == "organization") {
		updateUserTypeInForm("organization", "user", "org-user-type", "organization");
		if (!displayBoth) {
			hideFields("org-select", "org-uni");
		}
  }
}

/* helper function to display the correct form and set user type */
function updateUserTypeInForm(display, hide, toggleSection, toggleValue) {
	document.getElementById(display).style.display = "block";
	document.getElementById(hide).style.display = "none";
	document.getElementById(toggleSection).value = toggleValue;
}

/* helper function to hide/display the correct fields in forms */
function hideFields(selectField, universityField) {
	document.getElementById(selectField).style.display = "none";
  document.getElementById(universityField).style.display="none";
}

/* get the saved events for individual users */
function getIndividualEvents() {
  var userType = sessionStorage.getItem("user-type");
  if (userType == null) {
    getUserType();
  }
  fetch('get-' + userType).then(response => response.json()).then((data) => {
    data[0].savedEvents.forEach((event) => createSavedEventElement(event));
    displayMain(true);
  });
}

/* get the saved organizations for individual users */
function getIndividualOrganizations() {
  var userType = sessionStorage.getItem("user-type");
  if (userType == null) {
    getUserType();
  }
  fetch('get-' + userType).then(response => response.json()).then((data) => {
    getSavedOrgElements(data[0].savedOrganizations);
    displayMain(true);
  });
}

/* function to return the list of corresponding saved organizations */
function getSavedOrgElements(email) {
  fetch('get-saved-organizations?emails=' + email).then(response => response.json()).then((data) => {
    data.forEach((org) => createSavedOrgElement(org));
    });
}

/* Function to create the individual organization display divs*/
function createSavedOrgElement(data) {
  const divElement = document.createElement('div');
  divElement.setAttribute("class", "item-container general-container");
 
  const h3ElementName = document.createElement('h3');
  h3ElementName.innerText = data.name;
  divElement.appendChild(h3ElementName);

  const h5ElementEmail = document.createElement('h5');
  h5ElementEmail.innerText = data.email;
  divElement.appendChild(h5ElementEmail);

  const h5ElementBio = document.createElement('h5');
  h5ElementBio.innerText = data.description;
  divElement.appendChild(h5ElementBio);

  // create delete organization form
  const form = document.createElement("form");
  form.setAttribute("method", "POST");
  form.setAttribute("action", "delete-saved-organization?&organization-id=" + data.datastoreId);
  const button = document.createElement('button');
  button.innerText = "Unsave this organization";
  button.setAttribute("type", "submit");
  
  form.appendChild(button);
  divElement.appendChild(form);
  document.getElementById("saved-orgs").appendChild(divElement);
}

/* create the individual event containers for displaying events*/
function createSavedEventElement(event) {
  const divElement = document.createElement('div');
  divElement.setAttribute("class", "item-container general-container");
 
  const h3ElementName = document.createElement('h3');
  h3ElementName.innerText = event;
  divElement.appendChild(h3ElementName);

  // create delete event form
  const form = document.createElement("form");
  form.setAttribute("method", "POST");
  form.setAttribute("action", "delete-saved-event?event-name=" + event);
  const button = document.createElement('button');
  button.innerText = "Unsave this event";
  button.setAttribute("type", "submit");
  
  form.appendChild(button);
  divElement.appendChild(form);
  document.getElementById("saved-events").appendChild(divElement);
}

/* Function to control form display using button */
function revealForm() {
	var userType = sessionStorage.getItem("user-type");
	if (userType == null) {
		getUserType();
    if (sessionStorage.getItem("user-type") == null) {
        displayForm("individual", true);
        return;
    }
	} 
    displayForm(userType, false);
}

/* Function to close form display after submission */
function closeForm() {
	document.getElementById("user").style.display = "none";
	document.getElementById("organization").style.display = "none";
}

/* function to generate divs for the calendar */
function createCalendar() {
  const calendar = document.getElementById("calendar");
  for (var i = 0; i < 14; i++) {
    var nextDay = new Date();
    var today = new Date();
    nextDay.setDate(today.getDate() + i);
    const dateDiv = document.createElement('div');
    dateDiv.setAttribute("class", "date general-container");
    const dateDisplay = document.createElement('p');
    dateDisplay.innerText = nextDay.toDateString();
    dateDiv.appendChild(dateDisplay);
    calendar.append(dateDiv);
  }
}
/*
 * If element is selected, border will change from white to green
 * @param element id
 */
function toggleBorderSelection(elementId) {
  var element = document.getElementById(elementId);
  if (element.nodeName == 'BUTTON') {
    element.classList.toggle('selected');
  } else if (element.nodeName == 'SELECT') {
    if (element.value != '') {
      element.classList.add('selected');
    } else {
      element.classList.remove('selected');
    }
  }
}

/*
 * Reviews are temp on event listing so remove to see finalized event search page
 */
function removeReviews() {
  const reviews = document.getElementsByClassName('reviews');
  while(reviews.length > 0){
    reviews[0].parentNode.removeChild(reviews[0]);
  }
}

