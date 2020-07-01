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

function createMap() {
  var nycLatLng = {lat: 40.730610, lng: -73.935242};
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: nycLatLng, zoom: 11});
} 

/** authenticate user and return email */
function checkAuth(){
  // send request for information on login status
  // if request not working, default to preset value
  var email = "";
  fetch('_gcp_iap/identity').then(response => response.json()).then((data) => {
      email = data["email"].substring(20);
  });
  if (email == "") {
      email = "jenny@google.com";
  }
  return email;
}

/* get the user information for the profile page */
function getUser() {
  var email = checkAuth();
  
   // prefill the email in the form so that user cannot edit their email
  document.getElementById("email-form-display").innerText = email;
  document.getElementById("org-email-form-display").innerText = email;
  document.getElementById("email-form").value = email;
  document.getElementById("org-email-form").value = email;

  var userType = sessionStorage.getItem("user-type");

  // if there is no userType stored in this session, that means this is a new user
  if (userType == null) {
    getUserType();
  } else {
    if (userType == "organization") {
      fetch('get-organization?email=' + email).then(response => response.json()).then((data) => {
        createProfile(data, true);
        sessionStorage.setItem("user-type", data[0].userType);
      });
    } else if (userType == "individual") {
      fetch('get-individual?email=' + email).then(response => response.json()).then((data) => {
        createProfile(data, false);
        sessionStorage.setItem("user-type", data[0].userType);
      });  
    }
    displayMain(true);
  }
}

function displayMain(display) {
  if (display) {
    document.getElementById("profile-section").style.display = "block";
    document.getElementById("no-profile").style.display = "none";
  } else {
    document.getElementById("profile-section").style.display = "none";
    document.getElementById("no-profile").style.display = "block";
  }
}
/* get and store the user type (individual or organization). With this we determine whether
this user exists in our database or not */
function getUserType() {
    var email = checkAuth();

    /* Since there is no way to know beforehand whether the user is an organization 
    or an individual, we have to do two fetches to check the organization entities and
    the user entities */
    fetch('get-organization?email=' + email).then(response => response.json()).then((data) => {
      if (data.length != 0) {
          sessionStorage.setItem("user-type", data[0].userType);
          displayMain(true);
      } else {
        fetch('get-individual?email=' + email).then(response => response.json()).then((newData) => {
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
    const emailFormContainer = document.getElementById("email-form");
    emailFormContainer.value = data[0].email;

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

    // addresses the different fields for each user type
    if (isOrganization) {
      createOrgProfile(data);
    } else {
      createIndividualProfile(data);
    }

}

/* populate individual specific fields of the profile */
function createIndividualProfile(data) {
  document.getElementById("university-form-display").innerText = data[0].university;
  const firstNameContainer = document.getElementById("firstname");
  const pElementFirstName = document.createElement('p');
  pElementFirstName.innerText = "First Name: " + data[0].firstName;
  firstNameContainer.appendChild(pElementFirstName);
  document.getElementById("ind-firstname").value = data[0].firstName;

  const lastNameContainer = document.getElementById("lastname");
  const pElementLastName = document.createElement('p');
  pElementLastName.innerText = "Last Name: " + data[0].lastName;
  lastNameContainer.appendChild(pElementLastName);
  document.getElementById("ind-lastname").value = data[0].lastName;

  document.getElementById("org-name").style.display = "none";
  document.getElementById("description").style.display = "none";
}

/* populate organization specific fields of the profile */
function createOrgProfile(data) {
  document.getElementById("org-university-form-display").innerText = data[0].university;
  const nameContainer = document.getElementById("org-name");
  const pElementName = document.createElement('p');
  pElementName.innerText = "Organization Name: " + data[0].name;
  nameContainer.appendChild(pElementName);
  document.getElementById("org-form-name").value = data[0].name;

  const descriptionContainer = document.getElementById("description");
  const pElementDescription = document.createElement('p');
  pElementDescription.innerText = "Description: " + data[0].description;
  descriptionContainer.appendChild(pElementDescription);
  document.getElementById("org-description").value = data[0].description;

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
function hideFields(selectField, uniField) {
	document.getElementById(selectField).style.display = "none";
  document.getElementById(uniField).style.display="none";
}

/* get the saved events for individual users */
function getIndividualEvents() {
  var email = checkAuth();
  document.getElementById("temp-email").value = email;
  var userType = sessionStorage.getItem("user-type");
  if (userType == null) {
    getUserType();
  }
  fetch('get-' + userType + '?email=' + email).then(response => response.json()).then((data) => {
    data[0].savedEvents.forEach((event) => createSavedEventElement(event, email));
    document.getElementById("no-profile").style.display = "none";
    document.getElementById("main").style.display = "block";
  });
}

/* get the saved organizations for individual users */
function getIndividualOrganizations() {
  var email = checkAuth();
	document.getElementById("temp-email").value = email;
  var userType = sessionStorage.getItem("user-type");
  if (userType == null) {
    getUserType();
  }
  fetch('get-' + userType + '?email=' + email).then(response => response.json()).then((data) => {
    getSavedOrgElements(data[0].savedOrganizations, email);
    document.getElementById("no-profile").style.display = "none";
    document.getElementById("main").style.display = "block";
  });
}

/* function to return the list of correponding saved organizations */
function getSavedOrgElements(email, userEmail) {
  fetch('get-saved-organizations?emails=' + email).then(response => response.json()).then((data) => {
    data.forEach((org) => createSavedOrgElement(org, userEmail));
    });
}

/* Function to create the individual organization display divs*/
function createSavedOrgElement(data, email) {
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
  form.setAttribute("action", "delete-saved-organization?email=" + email + "&organization-email=" + data.email);
  const button = document.createElement('button');
  button.innerText = "Unsave this organization";
  button.setAttribute("type", "submit");
  
  form.appendChild(button);
  divElement.appendChild(form);
  document.getElementById("saved-orgs").appendChild(divElement);
}

/* create the individual event containers for displaying events*/
function createSavedEventElement(event, email) {
  const divElement = document.createElement('div');
  divElement.setAttribute("class", "item-container general-container");
 
  const h3ElementName = document.createElement('h3');
  h3ElementName.innerText = event;
  divElement.appendChild(h3ElementName);

  // create delete event form
  const form = document.createElement("form");
  form.setAttribute("method", "POST");
  form.setAttribute("action", "delete-saved-event?email=" + email + "&event-name=" + event);
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

