function createMap() {
  var nycLatLng = {lat: 40.730610, lng: -73.935242};
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: nycLatLng, zoom: 11});
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
 * @param appendElement element to append
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

/**
 * Toggle advanced filters
 */
function showMore() {
  const filterElement = document.getElementById('additionalFilters');
  const button = document.getElementById('filterButton');

  if (filterElement.style.display == 'block') {
    filterElement.style.display = 'none';
    button.innerText = 'MORE';
  } else {
    filterElement.style.display = 'block';
    button.innerText = 'LESS';
  }
}

/*
 * If element is selected, border will change colors to show selection
 * @param elementID element id to add selected class
 */
function toggleBorderSelection(elementId) {
  var element = document.getElementById(elementId);
  if (element.nodeName == 'SELECT' || element.nodeName == 'INPUT' ) {
    if (element.value != '') {
      element.classList.add('selected');
    } else {
      element.classList.remove('selected');
    }
  } else {
    element.classList.toggle('selected');
  }
}

/*
 * Create elements from list of event types
 * @param appendElementId id of DOM element
 * @param elementType type of element to create
 */
function createEventTypeElements(appendElementId, elementType) {
  var eventTypeValues = ["forum", "game", "movie", "party", "performance", "speaker", "volunteer", "workshop", "other"];
  var appendElement = document.getElementById(appendElementId);

  eventTypeValues.forEach(function (item, index) {
    var element = createElement(appendElement, elementType, item);
    element.value = item;
    });
}

/*
 * Create elements from list of organization types
 * @param appendElementId id of DOM element
 * @param elementType type of element to create
 */
function createOrgTypeElements(appendElementId, elementType) {
  var orgTypeValues = ["academic", "athletic", "arts", "cultural", "professional", "political", "service", "studentgov"];
  var orgTypeNames = ["Academic", "Athletic", "Arts", "Cultural", "Professional", "Political", "Service", "Student Government"];
  var appendElement = document.getElementById(appendElementId);

  orgTypeValues.forEach(function (item, index) {
    var value = orgTypeValues[index];
    var element = createElement(appendElement, elementType, orgTypeNames[index]);
    element.value = value;

    if (elementType == "button") {
      element.id = value;
      element.className = "orgFilter";
      element.addEventListener('click', () => {
         toggleBorderSelection(value);
      });
    }
  });
}