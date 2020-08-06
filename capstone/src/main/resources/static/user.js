/**
  * highlights active tab for general nav
  * @param tab the profile navigation tab that needs to be active
  */
function profileNavActive(tab) {
  if (tab == "profile") {
    document.getElementById("ind-profile").setAttribute("class", "active-highlighted");
    document.getElementById("org-profile").setAttribute("class", "active-highlighted");
  } else {
    document.getElementById(tab).setAttribute("class", "active-highlighted");
  }
}

/**
  * highlights active tab for profile nav
  * @param tab the general navigation tab that needs to be active
  */
function generalNavActive(tab) {
  fetch('user-info').then(response => response.json()).then((data) => {
    if (data.userType == "individual") {
      document.getElementById("general-recommended").style.display="inline-block";
    }
    document.getElementById(tab).setAttribute("class", "active");
  });
}

/**
  * highlights active tab for recommendation nav
  * @param tab the tab in the recommendation nav that needs to be active
  */
function recNavActive(tab) {
  document.getElementById(tab).setAttribute("class", "active");
}

/**
  * get the user information for the profile page
  * @param fillForm boolean indicating whether the profile form should be prefilled
  * @param generalTab the general tab active right now
  * @param profileTab the profile tab active right now
  */
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
    hideSpinner();
  }).catch((error) => {
    // log error
    hideSpinner();
  });
}

/**
  * sets up account page
  * @param isOrganization boolean indicating whether current user is an organization
  * @param fillForm boolean indicating whether the profile form should be prefilled
  * @param data data of current user
  * @param navToBeHidden the current nav being hidden
  * @param navToBeDisplayed the current nav being displayed
  */
function setUpAccountPage(isOrganization, fillForm, data, navToBeHidden, navToBeDisplayed) {
  createProfile(data, fillForm, isOrganization);
  displayNavToggle(navToBeHidden, navToBeDisplayed);
}

/**
 * Add the image form to the profile page
 * @param formName the name of the profile form that the image field is added to
 */
function addImageField(formName) {
  fetch('upload-image').then(response => response.text()).then((data) => {
    const form = document.getElementById(formName);
    form.innerHTML = data;
  });
}

/**
 * upon submission, hide the image form
 */
function closeImageForm() {
  document.getElementById("user-image-form").style.display = "none";
  document.getElementById("user").style.display = "none";
  document.getElementById("organization").style.display = "none";
  // refresh the page 8 seconds after submission
  setTimeout(function () {
    document.getElementById("get-image").setAttribute("src", "get-image?" + new Date().getTime());
    }, 8000);
}

/**
 * function to toggle between displaying user profile and displaying an error message
 * @param display boolean indicator of whether to display the main content of the page
 */
function displayMain(display) {
  if (display) {
    document.getElementById("no-profile").style.display = "none";
  } else {
    document.getElementById("no-profile").style.display = "block";
  }
}

/**
 * toggle between the two nav bars
 * @param navToBeHidden the navigation bar that needs to be hidden
 * @param navToBeDisplayed the navigation bar that needs to be shown
 */
function displayNavToggle(navToBeHidden, navToBeDisplayed) {
  document.getElementById(navToBeDisplayed).style.display="block";
  document.getElementById(navToBeHidden).style.display="none";
}

/**
 * creates and populates the user profile
 * @param data the current user's data
 * @param fillForm the boolean indicator of whether the profile form needs to be prefilled
 * @param isOrganization boolean indication of whether current user is an organization
 */
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

/**
 * populate individual specific fields of the profile
 * @param data current user's data
 * @param fillForm boolean indicator of whether the profile form needs to be prefilled
 */
function createIndividualProfile(data, fillForm) {
  const nameContainer = document.getElementById("name");
  const pElementName = document.createElement('h1');
  pElementName.innerText = data.firstName + " " + data.lastName;
  nameContainer.appendChild(pElementName);

  // hide fields that pertain to organizations only
  document.getElementById("description").style.display = "none";
  document.getElementById("rank").style.display = "none";

  if (fillForm) {
    // prefill form
    document.getElementById("ind-firstname").value = data.firstName;
    document.getElementById("ind-lastname").value = data.lastName;
    document.getElementById("university-form-display").innerText = data.university.name;
    document.getElementById("ind-uni").value = data.university.name;
  }
}

/**
 * populate organization specific fields of the profile
 * @param data current user's data
 * @param fillForm boolean indicator of whether the profile form needs to be prefilled
 */
function createOrgProfile(data, fillForm) {
  const nameContainer = document.getElementById("name");
  const pElementName = document.createElement('h1');
  pElementName.innerText = data.name;
  nameContainer.appendChild(pElementName);

  const descriptionContainer = document.getElementById("description");
  const pElementDescription = document.createElement('p');
  pElementDescription.innerText = "About Us: " + data.description;
  descriptionContainer.appendChild(pElementDescription);

  const rankContainer = document.getElementById("rank");
  const pElementRank = document.createElement("p");
  pElementRank.innerText = "There are " + data.rank + " users following";
  rankContainer.appendChild(pElementRank);

  if (fillForm) {
    // prefill form
    document.getElementById("org-form-name").value = data.name;
    document.getElementById("org-university-form-display").innerText = data.university.name;
    document.getElementById("org-description").value = data.description;
    document.getElementById("org-uni").value = data.university.name;
    document.getElementById("org-type").value = data.orgType;
  }
}

/**
 * function used to toggle after a change in the selected user type input
 * @param formUserType the usertype that the profile form field needs to be preset to
 */
function toggleForm(formUserType) {
  var userType = document.getElementById(formUserType).value;
  displayForm(userType, true);
}

/**
 * displays form according to user type
 * a new user will be able to toggle, but a returning user will not
 * @param userType current user's user type
 * @param displayBoth boolean indicator of whether both organization and individual forms need to be display-able
 */
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

/**
 * displays the correct form according to specified parameters and set user type
 * @param formToBeDisplayed form that should be displayed
 * @param formToBeHidden form that should be hidden
 * @param toggleSection section of the profile form that needs to be changed
 * @param toggleValue the value to change to
 */
function updateUserTypeInForm(formToBeDisplayed, formToBeHidden, toggleSection, toggleValue) {
	document.getElementById(formToBeDisplayed).style.display = "block";
	document.getElementById(formToBeHidden).style.display = "none";
	document.getElementById(toggleSection).value = toggleValue;
}

/**
 * hides and displays the specified fields in forms
 * @param userTypeSelectField the usertype select field in profile form that needs to be hidden
 * @param universityField the university field that needs to be hidden
 */
function hideFields(userTypeSelectField, universityField) {
	document.getElementById(userTypeSelectField).style.display = "none";
  document.getElementById(universityField).style.display="none";
}

/**
 * get the saved events or organizations for individual users
 * @param isEvent boolean indicator of whether it is fetching events
 */
function getIndividualEventsOrOrganizations(isEvent) {
  fetch('user-info').then(response => response.json()).then((data) => {
    if(data.userType == "individual") {
      if (isEvent) {
        const eventDiv = document.getElementById("saved-events");
        eventDiv.innerHTML = '';
        data.savedEvents.forEach((event) => createEventElement(eventDiv, event, true, true, data.email));
      } else {
        const orgDiv = document.getElementById("saved-orgs");
        data.organizations.forEach((org) => createSavedOrgElement(orgDiv, org, true, true));
      }
      displayMain(true);
    } else {
      displayMain(false);
    }
    hideSpinner();
  }).catch((error) => {
    // log error
    hideSpinner();
  });
}

/**
 * Function to create the individual organization display divs
 * @param orgListElement the div that is going to display all the events
 * @param data data of the organization that we are adding to the display
 * @param deleteAllowed boolean indicator of whether there should be a delete button displayed
 * @param displayButton boolean indicator of whether any button should be displayed
 */
function createSavedOrgElement(orgListElement, data, deleteAllowed, displayButton) {
  const orgElement = createElement(orgListElement, 'li', '');
  orgElement.className = 'event';

  // Click to redirect to public organization page
  orgElement.addEventListener('click', () => {
    window.location="publicprofile.html?organization-id=" + data.datastoreId;
  });

  createElement(orgElement, 'h3', data.name);
  createElement(orgElement, 'p', "There are " + data.rank + " users following this organization");

  if (displayButton) {
     const form = deleteAllowed ? createDeleteButton(data.datastoreId) : createSaveButton(data);
     orgElement.appendChild(form);
  }
}

/**
 * create delete buttons for the organization divs
 * @param datastoreId the id of the organization that is to be deleted from the user list
 */
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

/**
 * create save buttons for the organization divs
 * @param data data of the organization that is going to be saved to the user's list
 */
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

/**
 * creates an unsave button for event
 * @param divElement the div that this button is added to
 * @param event the event that is displayed on the current div
 */
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

/**
 * creates a save button for event
 * @param divElement the div that this button is added to
 * @param event the event that is displayed on the current div
 */
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

/**
 * creates an edit button and a delete button for event
 * @param divElement the div that this button is added to
 * @param event the event that is displayed on the current div
 */
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

/**
 * Function to control form display using button
 */
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

/**
 * Function to close form display after submission
 */
function closeForm() {
	document.getElementById("user").style.display = "none";
	document.getElementById("organization").style.display = "none";
}

/* function to get all events hosted by the current organization */
function getOrganizationEvents() {
  fetch('user-info').then(response => response.json()).then((data) => {
    if (data.userType == "organization") {
      const eventDiv = document.getElementById("created-events");
      data.events.forEach((event) => createEventElement(eventDiv, event, false, true, data.email));
      displayMain(true);
    } else {
      displayMain(false);
    }
    hideSpinner();
  }).catch((error) => {
    // log error
    hideSpinner();
  });
}

/**
 * Function to support searching for organizations by name
 */
function searchOrg() {
  fetch('user-info').then(response => response.json()).then((data) => {
    if (data.userType == "unknown") {
      displayMain(false);
      hideSpinner();
    } else {
      var displaySaveButton = data.userType == "individual";
      var name = document.getElementById("search-org").value;
      var selectedOrgTypes = document.querySelectorAll('.orgFilter.selected');
      var orgTypes = new Array();
      for(var value of selectedOrgTypes.values()) {
        orgTypes.push(value.value);
      }
      fetch('search-organization?name=' + name + "&university=" + data.university.name + "&orgTypes=" + orgTypes).then(response => response.json()).then((organizations) => {
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
        hideSpinner();
      }).catch((error) => {
        // log error
        hideSpinner();
      });
    }
  });
}

/**
 * function to generate divs for the calendar
 */
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
      userData.savedEvents.forEach((event) => {
        createCalendarEvent(event, today, endDate, userData.email);
      });
      fetch('get-all-org-events').then(response => response.json()).then((data) => {
        data.forEach((event) => {
          createCalendarEvent(event, today, endDate, userData.email);
        });
        hideSpinner();
      }).catch((error) => {
        // log error
        hideSpinner();
      });
    } else {
      displayMain(false);
      hideSpinner();
    }
  });
}

/**
 * creates calendar events
 * @param event event that may be added to the calendar
 * @param today today's date
 * @param endDate the date that is seven days from today
 * @param userEmail email of the current user
 */
function createCalendarEvent(event, today, endDate, userEmail) {
  var eventDate = new Date(event.eventDateTime);
  // only add events to the container if they are future events
  if (eventDate.getTime() > today.getTime() && eventDate.getTime() < endDate.getTime()) {
    var diff = Math.floor((eventDate.getTime() - today.getTime()) / (1000 * 3600 * 24));
    const generalDateDiv = document.getElementById("date" + diff);
    createEventElement(generalDateDiv, event, true, true, userEmail);
  }
}

/**
 * function to create a public profile of an organization
  * @param isEventRefresh true if function refreshes events without updating profile details
 */
function getPublicProfile(isEventRefresh) {
  fetch('user-info').then(response => response.json()).then((userData) => {
     if (userData.userType != "unknown") {
       var searchParams = new URLSearchParams(location.search);
       var organizationId = searchParams.get("organization-id");
       var userType = userData.userType == "individual";
       if (organizationId != null) {
         fetch('get-public-profile?organization-id=' + organizationId).then(response => response.json()).then((data) => {
           if (!isEventRefresh) {
             createProfile(data, false, true);
           }
           const eventDiv = document.getElementById("hosted-events");
           eventDiv.innerHTML = '';
           data.events.forEach((event) => createEventElement(eventDiv, event, userType, false, userData.email));
           document.getElementById("public-image-a").setAttribute("href", "get-public-image?email=" + data.email);
           document.getElementById("public-image-img").setAttribute("src", "get-public-image?email=" + data.email);
           const reviewContainer = document.getElementById("org-review-container");
           reviewContainer.innerHTML = '';
           createReviewElement(reviewContainer, data, userType, userData.email);
           hideSpinner();
         }).catch((error) => {
           // log error
           hideSpinner();
         });
         displayMain(true);
       } else {
          hideSpinner();
       }
     } else {
       displayMain(false);
       hideSpinner();
     }
  });
}
/**
 * find the recommended events or organizations
 * @param recommendationType either recommending events or recommending organizations
 */
function findRecommended(recommendationType) {
  var count = document.getElementById('recommended-input').value;
  fetch('user-info').then(response => response.json()).then((data) => {
    if (data.userType != 'individual') {
      displayMain(false);
      hideSpinner();
    } else {
      recommend(count, recommendationType, data.email);
      displayMain(true);
    }
  }).catch((error) => {
    // log error
    hideSpinner();
  });
}

/**
 * adds the list of recommended events/organizations to the page
 * @param count the number of recommended items requested
 * @param recommendationType either recommending events or recommending organizations
 * @param email email of the current user
 */
function recommend(count, recommendationType, email) {
  fetch('get-recommended-'+ recommendationType + '-individual?count=' + count).then(response => response.json()).then((data) => {
    const recDiv = document.getElementById("recommended-section");
    recDiv.innerHTML = "";
    if (recommendationType == "events") {
      data.forEach((event) => createEventElement(recDiv, event, true, false, email));
    } else {
      data.forEach((org) => createSavedOrgElement(recDiv, org, false, true));
    }
    hideSpinner();
  }).catch((error) => {
    // log error
    hideSpinner();
  });
}

/**
 * displays spinner on the page and hide main content
 */
function showSpinner() {
  document.getElementById('load').style.display="block";
}

/**
 * hide spinner and displays main content
 */
function hideSpinner() {
  document.getElementById('load').style.display="none";
}

