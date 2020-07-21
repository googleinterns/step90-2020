/* Function to create Google Map */
async function createMap() {
  var princetonLatLng = {lat: 40.3428452, lng: -74.6568153};
  const campusMap = new google.maps.Map(
    document.getElementById('map'),
    {center: princetonLatLng, zoom: 16});
    infoWindow = new google.maps.InfoWindow;

   const response = await fetch('get-all-events');
   const jsonEvents = await response.json();
   jsonEvents.forEach(event => createMarker(event, campusMap));

/* Geolocation API */

//    if (navigator.geolocation) {
//      navigator.geolocation.getCurrentPosition(function(position) {
//        var pos = {
//          lat: position.coords.latitude,
//          lng: position.coords.longitude
//        };
//        infoWindow.setPosition(pos);
//        infoWindow.setContent('Location found.');
//        infoWindow.open(campusMap);
//        campusMap.setCenter(pos);
//      }, function() {
//        handleLocationError(true, infoWindow, campusMap.getCenter());
//      });
//    }
//    else {
//      handleLocationError(false, infoWindow, campusMap.getCenter());
//    }
  }

 function handleLocationError(browserHasGeolocation, infoWindow, pos) {
    infoWindow.setPosition(pos);
    infoWindow.setContent(browserHasGeolocation ?
                          'Error: The Geolocation service failed.' :
                          'Error: Your browser doesn\'t support geolocation.');
    infoWindow.open(map);
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
  })
}

