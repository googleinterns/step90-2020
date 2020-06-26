
/** checks whether the user is authenticated and adjust elements 
according to whether the user is logged in or logged out */
function checkAuth(){
  // send request for information on login status
  fetch('_gcp_iap/identity').then(response => response.json()).then((data) => {
    const userEmail = document.getElementById('email-form');
    // default value set for testing on dev server
    const email = "accounts.google.com:jennysheng@google.com";
    userEmail.innerText = email.substring(20);
    
  });
}

function getUser() {
    var userEmail = "";
    fetch('_gcp_iap/identity').then(response => response.json()).then((data) => {
      email = "accounts.google.com:jennysheng@google.com".substring(20);
    });
    fetch('get-user?email=' + "jennysheng@google.com").then(response => response.json()).then((data) => {
      createProfile(data);
  });
}

function createProfile(data) {
  const emailFormContainer = document.getElementById("email-form");
    emailFormContainer.innerText = data[0].email;
    const firstNameContainer = document.getElementById("firstname");
    const pElementFirstName = document.createElement('p');
    pElementFirstName.innerText = "First Name: " + data[0].firstName;
    firstNameContainer.appendChild(pElementFirstName);

    const lastNameContainer = document.getElementById("lastname");
    const pElementLastName = document.createElement('p');
    pElementLastName.innerText = "Last Name: " + data[0].lastName;
    lastNameContainer.appendChild(pElementLastName);

    const emailContainer = document.getElementById("email");
    const pElementEmail = document.createElement('p');
    pElementEmail.innerText = "Email: " + data[0].email;
    emailContainer.appendChild(pElementEmail);

    const userTypeContainer = document.getElementById("user-type");
    const pElementUserType = document.createElement('p');
    pElementUserType.innerText = "User Type: individual";
    userTypeContainer.appendChild(pElementUserType);

    const universityContainer = document.getElementById("university");
    const pElementUniversity = document.createElement('p');
    pElementUniversity.innerText = "University: " + data[0].university;
    universityContainer.appendChild(pElementUniversity);

    const descriptionContainer = document.getElementById("description");
    const pElementDescription = document.createElement('p');
    pElementDescription.innerText = "Bio: " + data[0].description;
    descriptionContainer.appendChild(pElementDescription);

    // going to be implemented in the future
    // const savedEventsContainer = document.getElementById("saved-events");
    // savedEventsContainer.innerHTML = '';
    // for (var i = 0; i < data["saved-events"].length; i++) {
    //   savedEventsContainer.appendChild(
    //   createDivElement(data["saved-events"][i]));
    // }
}

function createDivElement(event) {
  const divElement = document.createElement('div');
  divElement.setAttribute("class", "item-container");
 
  const h3ElementName = document.createElement('h3');
  h3ElementName.innerText = event;
  divElement.appendChild(h3ElementName);

}