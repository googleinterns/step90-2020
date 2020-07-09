/* get the user information for the profile page */
function getUser() {
  var userType = sessionStorage.getItem("user-type");

  // if there is no userType stored in this session, that means this is a new user
  if (userType == null) {
    getUserType();
  } else {
    if (userType == "organization") {
      fetch('get-organization').then(response => response.json()).then((data) => {
        document.getElementById("org-nav").style.display = "block";
        document.getElementById("individual-nav").style.display = "none";
        createProfile(data[0], true);
        sessionStorage.setItem("user-type", data[0].userType);
      });
    } else if (userType == "individual") {
      fetch('get-individual').then(response => response.json()).then((data) => {
        document.getElementById("org-nav").style.display = "none";
        document.getElementById("individual-nav").style.display = "block";
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
          document.getElementById("org-nav").style.display="block";
          document.getElementById("individual-nav").style.display="none";
          displayMain(true);
      } else {
        document.getElementById("org-nav").style.display="none";
        document.getElementById("individual-nav").style.display="block";
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
  fetch('get-' + userType).then(response => response.json()).then((data) => {
    const eventDiv = document.getElementById("saved-events");
    data[0].savedEvents.forEach((event) => eventDiv.appendChild(createSavedEventElement(event, true)));
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
    data[0].organizations.forEach((org) => createSavedOrgElement(org));
    displayMain(true);
  });
}

/* function to return the list of correponding saved organizations */
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

function getOrganizationEvents() {
  var userType = sessionStorage.getItem("user-type");
  if (userType == null) {
    getUserType();
  }
  document.getElementById("org-nav").style.display="block";
  fetch('get-' + userType).then(response => response.json()).then((data) => {
    const eventDiv = document.getElementById("created-events");
    data[0].events.forEach((event) => eventDiv.appendChild(createSavedEventElement(event), false));
    displayMain(true);
  });
}