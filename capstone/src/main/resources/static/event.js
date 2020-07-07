async function getEvents() {
  const response = await fetch('get-all-events');
  const jsonEvents = await response.json();
  const eventList = document.getElementById('events-container');

  jsonEvents.forEach(event => eventList.appendChild(createEvent(event)));
}

function createEvent(event) {
  const listEventElement = document.createElement('li');
  listEventElement.className = 'list-events';


  createEventAttribute(organizationId);
  createEventAttribute(eventTitle);
  createEventAttribute(eventDateTime);
  createEventAttribute(eventDescription);
  createEventAttribute(eventLatitude);
  createEventAttribute(eventLongitude);
  
  return listEventElement;
}

function createEventAttribute(attributeName) {
  attributeName = document.createElement('p');
  atttributeName.innerText = event.attributeName;
  listEventElement.appendChild(attributeName);
}

async function newEvent() {
  await fetch('save-event', {method: 'POST'});
}

