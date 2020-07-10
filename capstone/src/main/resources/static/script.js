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
    eventListElement.innerText = "Events";
    
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
  const nameElement = document.createElement('p');
  nameElement.innerText = event.eventTitle;
  /*
  Time
  Location
  Organization
  Description
  */
  eventElement.appendChild(nameElement);
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
function getUser() {
  var userType = sessionStorage.getItem("user-type");

  // if there is no userType stored in this session, that means this is a new user
  if (userType == null) {
    getUserType();
  } else {
    if (userType == "organization") {
      fetch('get-organization').then(response => response.json()).then((data) => {
        createProfile(data[0], true);
        sessionStorage.setItem("user-type", data[0].userType);
      });
    } else if (userType == "individual") {
      fetch('get-individual').then(response => response.json()).then((data) => {
        createProfile(data[0], false);
        sessionStorage.setItem("user-type", data[0].userType);
      });  
    }
    displayMain(true);
  }
}


/* function to toggle between displaying user profile and displaying an error message */
function displayMain(display) {
  if (display) {
    document.getElementById("main").style.display = "block";
    document.getElementById("no-profile").style.display = "none";
  } else {
    document.getElementById("main").style.display = "none";
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
function createProfile(data, isOrganization) {
    const emailContainer = document.getElementById("email");
    const pElementEmail = document.createElement('p');
    pElementEmail.innerText = "Email: " + data.email;
    emailContainer.appendChild(pElementEmail);

    const userTypeContainer = document.getElementById("user-type");
    const pElementUserType = document.createElement('p');
    pElementUserType.innerText = "User Type: " + data.userType;
    userTypeContainer.appendChild(pElementUserType);

    const universityContainer = document.getElementById("university");
    const pElementUniversity = document.createElement('p');
    pElementUniversity.innerText = "University: " + data.university;
    universityContainer.appendChild(pElementUniversity);

    // addresses the different fields for each user type
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
  pElementFirstName.innerText = "First Name: " + data.firstName;
  firstNameContainer.appendChild(pElementFirstName);
  
  const lastNameContainer = document.getElementById("lastname");
  const pElementLastName = document.createElement('p');
  pElementLastName.innerText = "Last Name: " + data.lastName;
  lastNameContainer.appendChild(pElementLastName);

  // prefill form
  document.getElementById("ind-firstname").value = data.firstName;
  document.getElementById("ind-lastname").value = data.lastName;
  document.getElementById("university-form-display").innerText = data.university;
  
  // hide fields that pertain to organizations only
  document.getElementById("org-name").style.display = "none";
  document.getElementById("description").style.display = "none";
}

/* populate organization specific fields of the profile */
function createOrgProfile(data) {
  const nameContainer = document.getElementById("org-name");
  const pElementName = document.createElement('p');
  pElementName.innerText = "Organization Name: " + data.name;
  nameContainer.appendChild(pElementName);

  const descriptionContainer = document.getElementById("description");
  const pElementDescription = document.createElement('p');
  pElementDescription.innerText = "Description: " + data.description;
  descriptionContainer.appendChild(pElementDescription);

  // prefill form
  document.getElementById("org-form-name").value = data.name;
  document.getElementById("org-university-form-display").innerText = data.university;
  document.getElementById("org-description").value = data.description;

  // hide fields that pertain to individual users
  document.getElementById("firstname").style.display = "none";
  document.getElementById("lastname").style.display = "none";
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

