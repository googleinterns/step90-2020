/* Function to prefill event information if editing event */
function loadEventInfo() {
  const event = window.location.hash.substring(1);
  if (event != "") {
    fetch('get-event?event-id=' + event).then(response => response.json()).then((data) => {
      document.getElementById("eventTitle").value = data.eventTitle;
      document.getElementById("eventDateTime").value = data.eventDateTime;
      document.getElementById("eventLatitude").value = data.eventLatitude;
      document.getElementById("eventLongitude").value = data.eventLongitude;
      document.getElementById("eventDescription").value = data.description;
      document.getElementById("event-id").value = data.datastoreId;
      if (data.foodAvailable == true) {
        document.getElementById("foodAvailable").checked = true;
      }
      if (data.requiredFee == true) {
        document.getElementById("requiredFee").checked = true;
      }
    });
  }
}


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

   fetch('get-filtered-events?universityName=' + universityName + '&foodAvailable=' + selectedFilter('food') +
   '&free=' + selectedFilter('free') + '&eventType=' + '&eventTitle=').then(response => response.json()).then((events) => {
    const eventListElement = setElementInnerText('events', ''); // Clear elements in div
    if (events.length > 0){
      events.forEach((event) => {
         createEventElement(eventListElement, event, isIndividual, false, data.email);
      })
    } else {
      createElement(eventListElement, 'p', 'No events fit your filters');
    }

  });
}

/**
 * Check if a filter has been selected
 * @param elementId Id of filter element
 * @return true if filter was selected, false if not
 */
function selectedFilter(elementId){
  if (document.getElementById(elementId).classList.contains('selected')) {
    return true;
  }
  return false;
}
/**
 * Create a formatted list of events
 * @param eventListElement DOM element to append
 * @param event event object
 * @param isIndividual if user is an individual user
 * @param userEvent if individual has saved the event or org owns the event
 * @param userEmail current user's email
 */
function createEventElement(eventListElement, event, isIndividual, userEvent, userEmail) {
  const eventElement = createElement(eventListElement, 'li', '');
  eventElement.className = 'event';

  // Click for event detail modal
  eventElement.addEventListener('click', () => {
    showEventPage(event, isIndividual, userEmail);
  });

  // Name
  createElement(eventElement, 'p', event.eventTitle);

  // Time
  var date = new Date(event.eventDateTime);
  createElement(eventElement, 'p', date.toString().substring(0, 21)); // Exclude GMT time zone offset

  // Location Using latitude as a filler until we finalize the location portion
  createElement(eventElement, 'p', event.eventLatitude);

  // Organization
  createElement(eventElement, 'p', event.organizationName);

  // Only for individual users can save/unsave events
  if (isIndividual) {
    if (userEvent) { // Individual has saved event
      createUnsaveEventButton(eventElement, event);
    } else {
      createSaveEventButton(eventElement, event);
    }
  } else {
    if (userEvent) {
      createEditAndDeleteEventButton(eventElement, event);
    }
  }
}

/**
 * Create a page to view event details
 * @param event event object
 * @param isIndividual if current user is an individual user
 * @param userEmail current user's email
 */
function showEventPage(event, isIndividual, userEmail) {
  fillEventDetails(event);
  const modal = document.getElementById('modal');
  modal.style.display = 'block';

  const extraDetailsContainer =  document.getElementById("details-container");
  extraDetailsContainer.innerHTML = '';
  createExtraDetailsElement(extraDetailsContainer, event);

  const reviewContainer = document.getElementById("review-container");
  reviewContainer.innerHTML = '';
  createReviewElement(event, isIndividual, userEmail);

  if (event.reviews.length) { // Format time to *** time ago
    timeago.render(document.querySelectorAll('.timeago'));
  }
}

function createExtraDetailsElement(appendElement, event) {
  if (event.foodAvailable) {
    createElement(appendElement, 'p', 'Food Available').className = 'eventDetail';
  }

  if (event.free) {
    createElement(appendElement, 'p', 'Free').className = 'eventDetail';
  }
}

/**
 * Populate event details in the modal
 * @param event event object
 */
function fillEventDetails(event) {
  var date = new Date(event.eventDateTime);

  setElementInnerText("eventName", event.eventTitle);
  setElementInnerText("eventTime", date.toString().substring(0, 21)); // Exclude GMT time zone offset
  setElementInnerText("eventLocation", event.eventLatitude);
  setElementInnerText("eventOrganization", event.organizationName);
  setElementInnerText("eventDescription", event.eventDescription);
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
      document.getElementById("event-id").value = data.datastoreId;
      if (data.foodAvailable == true) {
        document.getElementById("foodAvailable").checked = true;
      }
      if (data.requiredFee == true) {
        document.getElementById("requiredFee").checked = true;
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

/**
 * Fill an existing document element's inner text
 * @param elementId document element's id
 * @param innerText text for inner text
 * @return element with added text
 */
function setElementInnerText(elementId, innerText){
  const element = document.getElementById(elementId);
  element.innerText = innerText;
  return element;
}

/**
 * Create new element with inner text and appended to an element
 * @param elementElement element to append
 * @param elementType element to create
 * @param innerText text for inner text
 * @return created element
 */
function createElement(appendElement, elementType, innerText){
  const element = document.createElement(elementType);
  element.innerText = innerText;
  appendElement.appendChild(element);
  return element;
}