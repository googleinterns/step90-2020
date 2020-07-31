/* Function to create Google Map and markers in map.html page */
 async function createMap() {
   var user;
   const response = await fetch('user-info');
   const jsonUser = await response.json();
   user = jsonUser.userType;

   const universityResponse = await fetch('get-university-map?userType=' + user);
   const jsonUniversity = await universityResponse.json();
   const campusMap = generateCampusMap(jsonUniversity);

   const eventResponse = await fetch('get-map-events');
   const jsonEvents = await eventResponse.json();
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
  async function createEventPlacementMap() {
     var user;
     const response = await fetch('user-info');
     const jsonUser = await response.json();
     user = jsonUser.userType;

     const universityMiniMapResponse = await fetch('get-university-map?userType=' + user);
     const university = await universityMiniMapResponse.json();

    var universityLocation = {lat: university.latitude, lng: university.longitude};
    const campusMap = new google.maps.Map(
      document.getElementById('eventMap'),
      {center: universityLocation, zoom: 16});

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

/* Create Google Map
 * @param university - jsonUniversity object
 */
 function generateCampusMap(university) {
    var universityCenter = {lat: university.latitude, lng: university.longitude};
    const campusMap = new google.maps.Map(
        document.getElementById('map'),
        {center: universityCenter,
         zoom: 16
        });
    return campusMap;
 }

/* Create a new marker for each event
 * @param event - event object
 * @param campusMap - Google Map object
 */
function createMarker(event,campusMap) {
  fetch("user-info").then(response => response.json()).then((data) => {
    if (data.userType != 'unknown') {
      var eventPosition = {lat: event.eventLatitude, lng: event.eventLongitude};
      const newMarker = new google.maps.Marker({
        map: campusMap,
        title: event.eventTitle,
        position: eventPosition,
      });
      var eventInfoWindow = createInfoWindow(event, campusMap, data.userType == 'individual', data.email);
      newMarker.addListener('click', function() {
        eventInfoWindow.open(campusMap, newMarker);
      });
    }
  });
}

/* Creates InfoWindow for event marker
 * @param event - event object
 * @param campusMap - Google Map object of campus
 * @param isIndividual - user type
 * @param userEmail - email of current user
 * return newInfoWindow = returns created info window
*/
function createInfoWindow(event, campusMap, isIndividual, userEmail) {
    var eventPosition = {lat: event.eventLatitude, lng: event.eventLongitude};
    var eventContent = document.createElement("div");
    eventContent.className = "map-info-window";
    createEventElement(eventContent, event, isIndividual, false, userEmail)
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
