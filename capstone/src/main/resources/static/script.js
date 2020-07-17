function createMap() {
  var nycLatLng = {lat: 40.730610, lng: -73.935242};
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: nycLatLng, zoom: 11});
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
