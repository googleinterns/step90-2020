async function getEvents() {
  const response = await fetch('get-all-events');
  const jsonEvents = await response.json();
  const eventList = document.getElementById('events-container');

  jsonEvents.forEach(event => eventList.appendChild(createEvent(event)));
}

function createEvent(event) {
  const listEventElement = document.createElement('li');
  listEventElement.className = 'list-events';

  // const organizationID = document.createElement('p');
  // organizationID.innerText = event.organizationID;
  // listEventElement.appendChild(organizationID);

  const organizationName = document.createElement('p');
  organizationName.innerText = event.organizationName;
  listEventElement.appendChild(organizationName);

  const eventTitle = document.createElement('p');
  eventTitle.innerText = event.eventTitle;
  listEventElement.appendChild(eventTitle);

  const eventDateTime = document.createElement('p');
  eventDateTime.innerText = event.eventDateTime;
  listEventElement.appendChild(eventDateTime);

  const eventDescription = document.createElement('p');
  eventDescription.innerText = event.eventDescription;
  listEventElement.appendChild(eventDescription);

  // const university = document.createElement('p');
  // university.innerText = event.university;
  // listEventElement.appendChild(university);

  const eventLatitude = document.createElement('p');
  eventLatitude.innerText = event.eventLatitude;
  listEventElement.appendChild(eventLatitude);

  const eventLongitude = document.createElement('p');
  eventLongitude.innerText = event.eventLongitude;
  listEventElement.appendChild(eventLongitude);
  
  return listEventElement;
}

async function newEvent() {
  await fetch('save-event', {method: 'POST'});
}

