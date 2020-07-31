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
 * If element is selected, border will change from white to green
 * @param element id
 */
function toggleBorderSelection(elementId) {
  var element = document.getElementById(elementId);
  if (element.nodeName == 'BUTTON') {
    element.classList.toggle('selected');
  } else if (element.nodeName == 'SELECT') {
    if (element.value != '') {
      element.classList.add('selected');
    } else {
      element.classList.remove('selected');
    }
  }
}

/*
 * Create elements from list of event types
 * @param appendElementId id of DOM element
 * @param elementType type of element to create
 */
function createEventTypeElements(appendElementId, elementType) {
  var eventTypeValues = ["forum", "game", "movie", "party", "performance", "speaker", "volunteer", "workshop"];
  var appendElement = document.getElementById(appendElementId);

  eventTypeValues.forEach(function (item, index) {
    var element = createElement(appendElement, elementType, item);
    element.value = item;
  });
}