async function getEvents() {
  const response = await fetch('get-all-events');
  const jsonEvents = await response.json();
  const eventList = document.getElementById('events-container');

  jsonEvents.forEach(event => eventList.appendChild(createEvent(event)));
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

