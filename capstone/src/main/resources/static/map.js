/* Function to create Google Map in map.html page */
async function createMap() {
  var princetonLatLng = {lat: 40.3428452, lng: -74.6568153};
  const campusMap = new google.maps.Map(
    document.getElementById('map'),
    {center: princetonLatLng, zoom: 16});

   const response = await fetch('get-all-events');
   const jsonEvents = await response.json();
   jsonEvents.forEach(event => createMarker(event, campusMap));
  }

function createMapForASingleEvent(event) {
  var princetonLatLng = {lat: 40.3428452, lng: -74.6568153};
    const campusMap = new google.maps.Map(
      document.getElementById('singleEventMap'),
      {center: princetonLatLng, zoom: 15});

    createMarker(event, campusMap);
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
         console.log(markerLatLng);
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
    position: eventPosition
  });
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
