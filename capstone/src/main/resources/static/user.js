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
      if (fillForm) {
        displayForm("individual", true);
      }
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

/* Add the image form to the profile page */
function addImageField(formName) {
  fetch('upload-image').then(response => response.text()).then((data) => {
    const form = document.getElementById(formName);
    form.innerHTML = data;
  });

}

/* upon submission, hide the image form */
function closeImageForm() {
  document.getElementById("user-image-form").style.display = "none";
  // refresh the page 8 seconds after submission
  setTimeout(function () {
    document.getElementById("get-image").setAttribute("src", "get-image?" + new Date().getTime());
    }, 8000);
}

/* function to toggle between displaying user profile and displaying an error message */
function displayMain(display) {
  if (display) {
    document.getElementById("no-profile").style.display = "none";
  } else {
    document.getElementById("no-profile").style.display = "block";
  }
}

/* toggle between the two nav bars */
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
    pElementUniversity.innerText = data.university.name;
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
    document.getElementById("university-form-display").innerText = data.university.name;
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
    document.getElementById("org-university-form-display").innerText = data.university.name;
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
  document.getElementById("user-image-form").style.display = "block";
  if (userType == "individual") {
    updateUserTypeInForm("user", "organization", "user-type-toggle", "individual");
    if (!displayBoth) {
      document.getElementById("university-form-display").style.display = "block";
      hideFields("user-select", "ind-uni");
      document.getElementById("university-form-display").style.display = "block";
    }
  } else if (userType == "organization") {
		updateUserTypeInForm("organization", "user", "org-user-type", "organization");
		document.getElementById("org-university-form-display").style.display = "block";
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
        data.savedEvents.forEach((event) => createEventElement(eventDiv, event, true, true, data.email));
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
    window.location="publicprofile.html?organization-id=" + data.datastoreId;
  });

  createElement(orgElement, 'h3', data.name);
  createElement(orgElement, 'p', data.description);

  if (displayButton) {
     const form = deleteAllowed ? createDeleteButton(data.datastoreId) : createSaveButton(data);
     orgElement.appendChild(form);
  }
}

/* create delete buttons for the organization divs */
function createDeleteButton(datastoreId) {
  const form = document.createElement("form");
  form.setAttribute("method", "POST");
  form.setAttribute("action", "delete-saved-organization?&organization-id=" + datastoreId);
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
  const editButton = document.createElement('button');
  editButton.innerText = "Edit this event";
  editButton.setAttribute("onclick", "window.location.href=" + "'event.html?event-id=" + event.datastoreId + "'");

  divElement.appendChild(editButton);

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
      data.events.forEach((event) => createEventElement(eventDiv, event, false, false, user.email));
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
      fetch('search-organization?name=' + name + "&university=" + data.university.name).then(response => response.json()).then((organizations) => {
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
  fetch('user-info').then(response => response.json()).then((userData) => {
    if (userData.userType == "individual") {
      const calendar = document.getElementById("calendar");

      var today = new Date();
      var endDate = new Date();
      endDate.setDate(today.getDate() + 8);
      const titleDiv = document.createElement('div');
      titleDiv.setAttribute("class", "events");
      createElement(titleDiv, 'p', 'A calendar of all the events from your saved organizations and saved events');
      for (var i = 0; i < 7; i++) {
        var nextDay = new Date();
        nextDay.setDate(today.getDate() + i);
        const dateDiv = document.createElement('div');
        dateDiv.setAttribute("class", "date general-container");
        createElement(dateDiv, 'p', nextDay.toDateString());
        const eventDiv = document.createElement('div');
        eventDiv.setAttribute("class", "date row");
        eventDiv.setAttribute("id", "date" + i);
        calendar.append(dateDiv);
        calendar.append(eventDiv);
      }
      data.savedEvents.forEach((event) => {
        createCalendarEvent(event, today, endDate, "coral", true, userData.email);
      });
      fetch('get-all-org-events').then(response => response.json()).then((data) => {
        data.forEach((event) => {
          createCalendarEvent(event, today, endDate, "cyan", false, userData.email);
        });
      });
    } else {
      displayMain(false);
    }
  });
}

/* helper function to create calendar events */
function createCalendarEvent(event, today, endDate, borderColor, isSavedEvent, userEmail) {
  var eventDate = new Date(event.eventDateTime);
  if (eventDate.getTime() > today.getTime() && eventDate.getTime() < endDate.getTime()) {
    var diff = Math.floor((eventDate.getTime() - today.getTime()) / (1000 * 3600 * 24));
    const generalDateDiv = document.getElementById("date" + diff);
    const newEvent = createEventElement(generalDateDiv, event, true, isSavedEvent, userEmail);
    if (!isSavedEvent) {
      newEvent.appendChild(createDeleteButton(event.organizationId));
    }
    newEvent.style.borderColor = borderColor;
  }
}
/* function to create a public profile of an organization */
function getPublicProfile() {
  fetch('user-info').then(response => response.json()).then((data) => {
     if (data.userType != "unknown") {
       var searchParams = new URLSearchParams(location.search);
       var organizationId = searchParams.get("organization-id");
       var userType = data.userType == "individual";
       if (organizationId != null) {
         fetch('get-public-profile?organization-id=' + organizationId).then(response => response.json()).then((data) => {
           createProfile(data, false, true);
           const eventDiv = document.getElementById("hosted-events");
           data.events.forEach((event) => createEventElement(eventDiv, event, userType, false, data.email));
           document.getElementById("public-image-a").setAttribute("href", "get-public-image?email=" + data.email);
           document.getElementById("public-image-img").setAttribute("src", "get-public-image?email=" + data.email);
         });
         displayMain(true);
       }
     } else {
       displayMain(false);
     }
  });
}

