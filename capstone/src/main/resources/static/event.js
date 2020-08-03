/**
 * Retrieves events from server if current user has a profile
 */
function getEvents() {
  showSpinner();
  fetch('user-info').then(response => response.json()).then((data) => {
    if (data.userType != "unknown") {
      loadEvents(data);
    } else { // no profile
      displayMain(false);
    }
    hideSpinner();
  }).catch((error) => {
    // log error
    hideSpinner();
  });
}

/**
 * Loads events if user has a profile
 * @param Current user data
 */
function loadEvents(data) {
  var isIndividual = data.userType == "individual";
  var universityName = data.university.name;

   fetch('get-filtered-events?universityName=' + universityName + '&eventTitle=' + selectedFilter('searchEvent') +
    '&eventType=' + selectedFilter('type') + '&energyLevel=' + selectedFilter('energyLevel') +
    '&location=' + selectedFilter('location') + '&foodAvailable=' + selectedFilter('food') + '&free=' + selectedFilter('free') +
    '&visitorAllowed=' + selectedFilter('visitorAllowed')).then(response => response.json()).then((events) => {

    const eventListElement = setElementInnerText('events', ''); // Clear elements in div

    if (events.length > 0){
      events.forEach((event) => {
         createEventElement(eventListElement, event, isIndividual, false, data.email);
      })
    } else {
      createElement(eventListElement, 'p', 'Sorry! No events fit your filters');
    }
  });
}

/**
 * Check if a filter has been selected
 * @param elementId Id of filter element
 * @return value of filter element if selected, empty string if not
 */
function selectedFilter(elementId){
  const filterElement = document.getElementById(elementId);
  if (filterElement.classList.contains('selected')) {
    return filterElement.value;
  }
  return '';
}

/**
 * Create a formatted list of events
 * @param eventListElement DOM element to append
 * @param event event object
 * @param isIndividual if user is an individual user
 * @param userEvent if individual has saved the event or org owns the event
 * @param userEmail current user's email
 */
async function createEventElement(eventListElement, event, isIndividual, userEvent, userEmail) {
  const eventElement = createElement(eventListElement, 'li', '');
  eventElement.className = 'event';

  // Name
  createElement(eventElement, 'p', event.eventTitle);

  // Time
  var date = new Date(event.eventDateTime);
  createElement(eventElement, 'p', date.toString().substring(0, 21)); // Exclude GMT time zone offset

  // Organization
  var organizationInfo = await fetch("get-public-profile?organization-id=" + event.organizationId);
  var organization = await organizationInfo.json();
  createElement(eventElement, 'p', organization.name);

  // Rank
  createElement(eventElement, 'p', "Number of people following this event: " + event.rank);

  // Click for event detail modal
  eventElement.addEventListener('click', () => {
    showEventPage(event, isIndividual, organization.name, userEvent, userEmail);
  });
}

/**
 * Create a page to view event details
 * @param event event object
 * @param isIndividual if current user is an individual user
 * @param organizationName event's organization name
 * @param userEvent if the user has saved/owns the event
 * @param userEmail current user's email
 */
function showEventPage(event, isIndividual, organizationName, userEvent, userEmail) {
  fillEventDetails(event, organizationName);
  const modal = document.getElementById('modal');
  modal.style.display = 'block';

  const extraDetailsContainer =  document.getElementById("details-container");
  extraDetailsContainer.innerHTML = '';
  createExtraDetailsElement(extraDetailsContainer, event);

  const userFunctionButtonsContainer =  document.getElementById("user-function-buttons-container");
  userFunctionButtonsContainer.innerHTML = '';


  // Only individual users can save/unsave events
  if (isIndividual) {
    if (userEvent) { // Individual has saved event
      createUnsaveEventButton(userFunctionButtonsContainer, event);
    } else {
      createSaveEventButton(userFunctionButtonsContainer, event);
    }
  } else {  // Event owners can edit and delete their events
    if (userEvent) {
      createEditAndDeleteEventButton(userFunctionButtonsContainer, event);
    }
  }
  const reviewContainer = document.getElementById("event-review-container");
  reviewContainer.innerHTML = '';
  createReviewElement(reviewContainer, event, isIndividual, "event", userEmail);

  if (event.reviews.length) { // Format time to *** time ago
    timeago.render(document.querySelectorAll('.timeago'));
  }
}

/*
 * Add the event's filterable details to modal
 * @param appendElement element to append new elements to
 * @param event event object
 */
function createExtraDetailsElement(appendElement, event) {
  if (event.eventType) {
    createElement(appendElement, 'p', event.eventType.charAt(0).toUpperCase() + event.eventType.slice(1)).className = 'eventDetail';
  }
  if (event.energyLevel) {
    createElement(appendElement, 'p', 'Energy Level ' + event.energyLevel).className = 'eventDetail';
  }
  if (event.location) {
    createElement(appendElement, 'p',  event.location.charAt(0).toUpperCase() + event.location.slice(1)).className = 'eventDetail';
  }
  if (event.foodAvailable) {
    createElement(appendElement, 'p', 'Food Available').className = 'eventDetail';
  }
  if (event.free) {
    createElement(appendElement, 'p', 'Free').className = 'eventDetail';
  }
  if (event.visitorAllowed) {
    createElement(appendElement, 'p', 'Visitors Allowed').className = 'eventDetail';
  }
}

/**
 * Populate event details in the modal
 * @param event event object
 * @param organizatiionName event's organization name
 */
function fillEventDetails(event, organizationName) {
  var date = new Date(event.eventDateTime);
  setElementInnerText("eventName", event.eventTitle);
  setElementInnerText("eventTime", date.toString().substring(0, 21)); // Exclude GMT time zone offset
  setElementInnerText("eventOrganization", organizationName);
  setElementInnerText("eventDescription", event.eventDescription);
  setElementInnerText("eventRank", "There are " + event.rank + " users following this event");
  createMapForASingleEvent(event);
}

/**
 * Closes modal if user clicks outside of it a page to view event details
 */
window.onclick = function(event) {
  const modal = document.getElementById('modal');
  if (event.target == modal) {
    modal.style.display = "none";
  }
}

/* Function to prefill event information if editing event */
function loadEventInfo() {
  var searchParams = new URLSearchParams(location.search);
  var event = searchParams.get("event-id");
  if (event != null) {
    fetch('get-event?event-id=' + event).then(response => response.json()).then((data) => {
      document.getElementById("eventTitle").value = data.eventTitle;
      document.getElementById("eventDateTime").value = data.eventDateTime;
      document.getElementById("eventLatitude").value = data.eventLatitude;
      document.getElementById("eventLongitude").value = data.eventLongitude;
      document.getElementById("eventDescription").value = data.eventDescription;
      document.getElementById("eventType").value = data.eventType;
      document.getElementById("energyLevel").value = data.energyLevel;
      document.getElementById("location").value = data.location;
      document.getElementById("event-id").value = data.datastoreId;
      if (data.foodAvailable == true) {
        document.getElementById("foodAvailable").checked = true;
      }
      if (data.requiredFee == true) {
        document.getElementById("free").checked = true;
      }
      if (data.visitorAllowed == true) {
        document.getElementById("visitorAllowed").checked = true;
       }
      hideSpinner();
    }).catch((error) => {
      // log error
      hideSpinner();
    });
  } else {
    hideSpinner();
  }
}