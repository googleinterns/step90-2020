async function getEvents() {
  const response = await fetch('get-all-events');
  const jsonEvents = await response.json();
  //const eventList = document.getElementById('events-container');

  //jsonEvents.forEach(event => eventList.appendChild(createEvent(event)));
}

function createEvent(event) {
  const listEventElement = document.createElement('li');
  listEventElement.className = 'list-events';


  createEventAttribute(event.organizationId, listEventElement);
  createEventAttribute(event.eventTitle, listEventElement);
  createEventAttribute(event.eventDateTime, listEventElement);
  createEventAttribute(event.eventDescription, listEventElement);
  createEventAttribute(event.eventLatitude, listEventElement);
  createEventAttribute(event.eventLongitude, listEventElement);
  createEventAttribute(event.foodAvaliable, listEventElement);
  createEventAttribute(event.requiredFee, listEventElement);
  
  return listEventElement;
}

function createEventAttribute(attributeValue, listEventElement) {
  const attributeValueElement = document.createElement('p');
  attributeValueElement.innerText = attributeValue;
  listEventElement.appendChild(attributeValueElement);
}

async function newEvent() {
  await fetch('save-event', {method: 'POST'});
}

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
      document.getElementById("event-id").value = data.datastoreID;
    });
  }

}

