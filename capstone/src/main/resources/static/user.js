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
        displayNavToggle("individual-nav", "org-nav");
        sessionStorage.setItem("user-type", data[0].userType);
      });
    } else if (userType == "individual") {
      fetch('get-individual').then(response => response.json()).then((data) => {
        createProfile(data[0], fillForm, false);
        displayNavToggle("org-nav", "individual-nav");
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
          displayNavToggle("individual-nav", "org-nav");
          setupAndStore(data[0], true);
      } else {
        displayNavToggle("org-nav", "individual-nav");
        fetch('get-individual').then(response => response.json()).then((newData) => {
          if (newData.length != 0) {
            // display information
            setupAndStore(newData[0]);
          } else {
            // user does not exist at all, display message to them to submit a profile
            displayMain(false);
            displayForm("individual", true);
          }
        });
      }
  });
}

function displayNavToggle(hide, display) {
  document.getElementById(display).style.display="block";
  document.getElementById(hide).style.display="none";
}

/** helper function to store information and set up display */
function setupAndStore(data) {
  sessionStorage.setItem("user-type", data.userType);
  sessionStorage.setItem("university", data.university);
  displayMain(true);
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
    updateUserTypeInForm("user", "oretuseranization", "user-type-toggle", "individual");
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
  if(userType == "individual") {
    fetch('get-' + userType).then(response => response.json()).then((data) => {
      const eventDiv = document.getElementById("saved-events");
      data[0].savedEvents.forEach((event) => eventDiv.appendChild(createSavedEventElement(event, true)));
      displayMain(true);
    });
  } else {
    displayMain(false);
  }

}

/* get the saved organizations for individual users */
function getIndividualOrganizations() {
  var userType = sessionStorage.getItem("user-type");
  if (userType == null) {
    getUserType();
  }
  if (userType == "individual") {
    fetch('get-' + userType).then(response => response.json()).then((data) => {
      data[0].organizations.forEach((org) => createSavedOrgElement(org));
      displayMain(true);
    });
  } else {
    displayMain(false);
  }
}

function getSavedOrgElements(email) {
  fetch('get-saved-organizations?emails=' + email).then(response => response.json()).then((data) => {
    const container = document.getElementById("saved-orgs");
    container.innerHTML = '';
    data.forEach((org) => container.appendChild(createSavedOrgElement(org, true)));
    });
}

/* Function to create the individual organization display divs*/
function createSavedOrgElement(data, deleteAllowed) {
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
  const form = deleteAllowed ? createDeleteButton(data) : createSaveButton(data);
  divElement.appendChild(form);

  return divElement;
}

/* create delete buttons for the organization divs */
function createDeleteButton(data) {
  const form = document.createElement("form");
  form.setAttribute("method", "POST");
  form.setAttribute("action", "delete-saved-organization?&organization-id=" + data.datastoreId);
  const button = document.createElement('button');
  button.innerText = "Unsave this organization";
  button.setAttribute("type", "submit");

  form.appendChild(button);
  return form;
}

/* create save buttons for the organization divs */
function createSaveButton(data) {
  const form = document.createElement("form");
  form.setAttribute("method", "POST");
  form.setAttribute("action", "add-saved-organization?organization-id=" + data.datastoreId);
  const button = document.createElement('button');
  button.innerText = "Save this organization";
  button.setAttribute("type", "submit");

  form.appendChild(button);
  return form;
}

/* create the individual event containers for displaying events*/
function createSavedEventElement(event, saveAllowed) {
  const divElement = document.createElement('div');
  divElement.setAttribute("class", "item-container general-container");

  const h3ElementName = document.createElement('h3');
  h3ElementName.innerText = event.eventTitle;
  divElement.appendChild(h3ElementName);

  const pElementTime = document.createElement('p');
  pElementTime.innerText = event.eventDateTime;
  divElement.appendChild(pElementTime);

  // create delete event form
  const form = saveAllowed? createUnsaveEventButton(event) : createEditEventButton(event);
  divElement.appendChild(form);

  return divElement;
}

/* creates an unsave button for event */
function createUnsaveEventButton(event) {
  const form = document.createElement("form");
  form.setAttribute("method", "POST");
  form.setAttribute("action", "delete-saved-event?event-id=" + event.datastoreID);
  const button = document.createElement('button');
  button.innerText = "Unsave this event";
  button.setAttribute("type", "submit");

  form.appendChild(button);
  return form;
}

/* creates an edit button for events */
function createEditEventButton(event) {
  const form = document.createElement("form");
  form.setAttribute("action", "event.html#" + event.datastoreID);
  const button = document.createElement('button');
  button.innerText = "Edit this event";
  button.setAttribute("type", "submit");

  form.appendChild(button);
  return form;
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

/* function to get all events hosted by the current organization */
function getOrganizationEvents() {
  var userType = sessionStorage.getItem("user-type");
  if (userType == null) {
    getUserType();
  }
  if (userType == "organization") {
    fetch('get-' + userType).then(response => response.json()).then((data) => {
      const eventDiv = document.getElementById("created-events");
      data[0].events.forEach((event) => eventDiv.appendChild(createSavedEventElement(event), false));
      displayMain(true);
    });
  } else {
    displayMain(false);
  }
}

/* Function to support searching for organizations by name */
function searchOrg() {
  var university = sessionStorage.getItem("university");
  if (university == null) {
    getUserType();
    if (sessionStorage.getItem("university") == null) {
      return;
    }
  }
  var name = document.getElementById("search-org").value;
  fetch('search-organization?name=' + name + "&university=" + university).then(response => response.json()).then((organizations) => {

    const orgListElement = document.getElementById('list-organizations');
    orgListElement.innerHTML = '';

    organizations.forEach((org) => {
      orgListElement.appendChild(createSavedOrgElement(org, false));
    })
  });
}

/* function to generate divs for the calendar */
function createCalendar() {
  const calendar = document.getElementById("calendar");

  var today = new Date();
  var endDate = new Date();
  endDate.setDate(today.getDate() + 8);
  for (var i = 0; i < 7; i++) {
    var nextDay = new Date();
    nextDay.setDate(today.getDate() + i);
    const dateDiv = document.createElement('div');
    dateDiv.setAttribute("class", "date general-container");
    const dateDisplay = document.createElement('p');
    dateDisplay.innerText = nextDay.toDateString();
    dateDiv.appendChild(dateDisplay);
    const eventDiv = document.createElement('div');
    eventDiv.setAttribute("class", "date row");
    eventDiv.setAttribute("id", "date" + i);
    calendar.append(dateDiv);
    calendar.append(eventDiv);
  }
  fetch('get-calendar-events').then(response => response.json()).then((data) => {
    data.forEach((event) => {
      var eventDate = new Date(event.eventDateTime);
      if (eventDate.getTime() > today.getTime() && eventDate.getTime() < endDate.getTime()) {
        var diff = eventDate.getDate() - today.getDate();
        const eventDisplay = createCalendarEventElement(event, eventDate);
        const generalDateDiv = document.getElementById("date" + diff);
        generalDateDiv.appendChild(eventDisplay);
      }
    });
  });
}

function createCalendarEventElement(event, eventTime) {
  const eventDisplay = document.createElement("div");
  eventDisplay.setAttribute("class", "event general-container col");
  const pElementTitle = document.createElement('p');
  pElementTitle.innerText = event.eventTitle;
  eventDisplay.appendChild(pElementTitle);
  const pElementTime = document.createElement('p');
  pElementTime.innerText = eventTime.toDateString();
  eventDisplay.appendChild(pElementTime);
  return eventDisplay;
}

/* Function to create the individual organization display divs*/
function createSavedOrgElement(data, deleteAllowed) {
 const divElement = document.createElement('div');
 divElement.setAttribute("class", "item-container general-container");

 const aElementName = document.createElement('a');
 aElementName.setAttribute("class", "public-org-name");
 aElementName.innerText = data.name;
 aElementName.setAttribute("href", "publicprofile.html#" + data.datastoreId);
 divElement.appendChild(aElementName);

 const h5ElementEmail = document.createElement('h5');
 h5ElementEmail.innerText = data.email;
 divElement.appendChild(h5ElementEmail);

 const h5ElementBio = document.createElement('h5');
 h5ElementBio.innerText = data.description;
 divElement.appendChild(h5ElementBio);

 // create delete organization form
 const form = deleteAllowed ? createDeleteButton(data) : createSaveButton(data);
 divElement.appendChild(form);

 return divElement;
}

function getPublicProfile() {
 var organizationId = window.location.hash.substring(1);
 fetch('get-public-profile?organization-id=' + organizationId).then(response => response.json()).then((data) => {
   createProfile(data, false, true);
   // remember to add events once the other PR is merged
 });
}

