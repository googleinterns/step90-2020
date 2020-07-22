/* helper function to highlight active tab for general nav */
function profileNavActive(tab) {
  if (tab == "profile") {
    document.getElementById("ind-profile").setAttribute("class", "active-highlighted");
    document.getElementById("org-profile").setAttribute("class", "active-highlighted");
  } else {
    document.getElementById(tab).setAttribute("class", "active-highlighted");
  }
}

/* helper function to highlight active tab for profile nav */
function generalNavActive(tab) {
  document.getElementById(tab).setAttribute("class", "active");
}

/* get the user information for the profile page */
function getUser(fillForm, generalTab, profileTab) {
  generalNavActive(generalTab);
  profileNavActive(profileTab);
  fetch('user-info').then(response => response.json()).then((data) => {
    // if there is no data returned, that means this is a new user
    if (data.userType == "unknown") {
      displayMain(false);
      displayForm("individual", true);
    } else {
      if (data.userType == "organization") {
        setUpAccountPage(true, fillForm, data, "individual-nav", "org-nav");
      } else {
        setUpAccountPage(false, fillForm, data, "org-nav", "individual-nav");
      }
      displayMain(true);
    }
  });
}

/* Helper function to setup account page */
function setUpAccountPage(isOrganization, fillForm, data, hide, display) {
  createProfile(data, fillForm, isOrganization);
  displayNavToggle(hide, display);
}

/* function to toggle between displaying user profile and displaying an error message */
function displayMain(display) {
  if (display) {
    document.getElementById("no-profile").style.display = "none";
  } else {
    document.getElementById("no-profile").style.display = "block";
  }
}

function displayNavToggle(hide, display) {
  document.getElementById(display).style.display="block";
  document.getElementById(hide).style.display="none";
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
    updateUserTypeInForm("user", "organization", "user-type-toggle", "individual");
    if (!displayBoth) {
      hideFields("user-select", "ind-uni");
      document.getElementById("university-form-display").style.display = "block";
    }
  } else if (userType == "organization") {
		updateUserTypeInForm("organization", "user", "org-user-type", "organization");
		if (!displayBoth) {
			hideFields("org-select", "org-uni");
			document.getElementById("org-university-form-display").style.display = "block";
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

/* get the saved events or organizations for individual users */
function getIndividualEventsOrOrganizations(isEvent) {
  fetch('user-info').then(response => response.json()).then((data) => {
    if(data.userType == "individual") {
      if (isEvent) {
        const eventDiv = document.getElementById("saved-events");
        data.savedEvents.forEach((event) => createEventElement(eventDiv, event, false, true, false));
      } else {
        const orgDiv = document.getElementById("saved-orgs");
        data.organizations.forEach((org) => createSavedOrgElement(orgDiv, org, true, true));
      }
      displayMain(true);
    } else {
      displayMain(false);
    }
  });
}

/* Function to create the individual organization display divs*/
function createSavedOrgElement(orgListElement, data, deleteAllowed, displayButton) {
  const orgElement = createElement(orgListElement, 'li', '');
  orgElement.className = 'event';

  // Click to redirect to public organization page
  orgElement.addEventListener('click', () => {
    window.location="publicprofile.html#" + data.datastoreId;
  });

  createElement(orgElement, 'h3', data.name);
  createElement(orgElement, 'p', data.description);

  if (displayButton) {
     const form = deleteAllowed ? createDeleteButton(data) : createSaveButton(data);
     orgElement.appendChild(form);
  }
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

/* creates an unsave button for event */
function createUnsaveEventButton(divElement, event) {
  const form = document.createElement("form");
  form.setAttribute("method", "POST");
  form.setAttribute("action", "delete-saved-event?event-id=" + event.datastoreId);
  const button = document.createElement('button');
  button.innerText = "Unsave this event";
  button.setAttribute("type", "submit");

  form.appendChild(button);
  divElement.appendChild(form);
}

/* creates a save button for event */
function createSaveEventButton(divElement, event) {
  const form = document.createElement("form");
  form.setAttribute("method", "POST");
  form.setAttribute("action", "add-saved-event?event-id=" + event.datastoreId);
  const button = document.createElement('button');
  button.innerText = "Save this event";
  button.setAttribute("type", "submit");

  form.appendChild(button);
  divElement.appendChild(form);
}

/* creates an edit button for events */
function createEditAndDeleteEventButton(divElement, event) {
  // create edit form
  const editForm = document.createElement("form");
  editForm.setAttribute("action", "event.html#" + event.datastoreId);
  const editButton = document.createElement('button');
  editButton.innerText = "Edit this event";
  editButton.setAttribute("type", "submit");

  editForm.appendChild(editButton);
  divElement.appendChild(editForm);

  divElement.appendChild(document.createElement('br'));

  // create delete form
  const deleteForm = document.createElement("form");
  deleteForm.setAttribute("action", "delete-organization-event?event-id=" + event.datastoreId);
  deleteForm.setAttribute("method", "POST");
  const deleteButton = document.createElement('button');
  deleteButton.innerText = "Delete this event";
  deleteButton.setAttribute("type", "submit");

  deleteForm.appendChild(deleteButton);
  divElement.appendChild(deleteForm);
}

/* Function to control form display using button */
function revealForm() {
	fetch('user-info').then(response => response.json()).then((data) => {
    if (data.userType == "unknown") {
      displayMain(false);
      displayForm("individual", true);
    } else {
      displayForm(data.userType, false);
    }
  });
}

/* Function to close form display after submission */
function closeForm() {
	document.getElementById("user").style.display = "none";
	document.getElementById("organization").style.display = "none";
}

/* function to get all events hosted by the current organization */
function getOrganizationEvents() {
  fetch('user-info').then(response => response.json()).then((data) => {
    if (data.userType == "organization") {
      const eventDiv = document.getElementById("created-events");
      data.events.forEach((event) => createEventElement(eventDiv, event, false, false, true));
      displayMain(true);
    } else {
      displayMain(false);
    }
  });
}

/* Function to support searching for organizations by name */
function searchOrg() {
  fetch('user-info').then(response => response.json()).then((data) => {
    if (data.userType == "unknown") {
      displayMain(false);
    } else {
      var displaySaveButton = data.userType == "individual";
      var name = document.getElementById("search-org").value;
      fetch('search-organization?name=' + name + "&university=" + data.university).then(response => response.json()).then((organizations) => {
        const orgListElement = document.getElementById('list-organizations');
        orgListElement.innerHTML = '';

        if (organizations.length == 0) {
          const pElementNone = document.createElement('p');
          pElementNone.innerText = "No organizations found. Please try to modify your search.";
          orgListElement.appendChild(pElementNone);
        } else {
          organizations.forEach((org) => {
            createSavedOrgElement(orgListElement, org, false, displaySaveButton);
          });
        }
      });
    }
  });
}

/* function to generate divs for the calendar */
function createCalendar() {
  fetch('user-info').then(response => response.json()).then((data) => {
    if (data.userType == "individual") {
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
            const generalDateDiv = document.getElementById("date" + diff);
            createEventElement(generalDateDiv, event, false, true, false);
          }
        });
      });
    } else {
      displayMain(false);
    }
  });
}

/* function to create a public profile of an organization */
function getPublicProfile() {
  fetch('user-info').then(response => response.json()).then((data) => {
     if (data.userType != "unknown") {
       var organizationId = window.location.hash.substring(1);
       var userType = data.userType == "individual";
       fetch('get-public-profile?organization-id=' + organizationId).then(response => response.json()).then((data) => {
         createProfile(data, false, true);
         const eventDiv = document.getElementById("hosted-events");
         data.events.forEach((event) => createEventElement(eventDiv, event, userType, false, false));
       });
       displayMain(true);
     } else {
       displayMain(false);
     }
  });

}

