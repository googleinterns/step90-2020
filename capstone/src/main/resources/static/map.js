/* Function to create Google Map in map.html page */
async function createMap() {

  var princetonLatLng = {lat: 40.3428452, lng: -74.6568153};
  const campusMap = new google.maps.Map(
    document.getElementById('map'),
    {center: princetonLatLng, zoom: 16});

   const eventResponse = await fetch('get-map-events');
   const jsonEvents = await eventResponse.json();
   jsonEvents.forEach(event => createMarker(event, campusMap));
  }

/* Function to create Mini Map in event.html page */
  function createEventPlacementMap() {
    var princetonLatLng = {lat: 40.3428452, lng: -74.6568153};
    const campusMap = new google.maps.Map(
      document.getElementById('eventMap'),
      {center: princetonLatLng, zoom: 16});

    var marker;
    google.maps.event.addListener(campusMap, 'click', function(newMarker) {
        if (marker) {
           marker.setMap(null);
        }
         marker = placeMarkerAndPan(newMarker.latLng, campusMap);
         var markerLatLng = newMarker.latLng.toString();
         document.getElementById('eventLatitude').value = newMarker.latLng.lat();
         document.getElementById('eventLongitude').value = newMarker.latLng.lng();
         google.maps.event.clearListeners(newMarker, 'click');
    });
 }

/* Create a new marker for each event
 * @param event - event object
 * @param campusMap - Google Map object
 */
function createMarker(event,campusMap) {
  var eventPosition = {lat: event.eventLatitude, lng: event.eventLongitude};
  const newMarker = new google.maps.Marker({
    map: campusMap,
    title: event.eventTitle,
    position: eventPosition,
  });
  var eventInfoWindow = createInfoWindow(event, campusMap);
  newMarker.addListener('click', function() {
    eventInfoWindow.open(campusMap, newMarker);
  });
}

/* Creates InfoWindow for event marker
 * @param event - event object
 * @param campusMap - Google Map object of campus
 * return newInfoWindow = returns created info window
*/
function createInfoWindow(event, campusMap) {
    var eventPosition = {lat: event.eventLatitude, lng: event.eventLongitude};
    var eventContent = '<p id=mapContent>'+ event.eventTitle + '</p>';
    const newInfoWindow = new google.maps.InfoWindow({
        content: eventContent,
        position: eventPosition
    });
    return newInfoWindow;
}

/* Creates Marker and Pans to location
 * @param latLng - latitude and longitude of marker
 * @param campusMap - Google Map of campus
 * return marker - returns created marker
*/
function placeMarkerAndPan(latLng, campusMap) {
    const marker = new google.maps.Marker({
        position: latLng,
        map: campusMap
    });
    campusMap.panTo(latLng);
    return marker;
}
