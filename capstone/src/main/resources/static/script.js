
/** checks whether the user is authenticated and adjust elements 
according to whether the user is logged in or logged out */
function checkAuth(){
  // send request for information on login status
  var email = "";
  fetch('_gcp_iap/identity').then(response => response.json()).then((data) => {
      email = data["email"].substring(20);
      document.getElementById("email-form-display").innerText = data[0].email;
  });
  if (email == "") {
      email = "jennysheng@google.com";
  }
  return email;
}

/* gets the user information from Datastore and display them in profile */
function getUser() {
    var email = checkAuth();
    fetch('get-user?email=' + email).then(response => response.json()).then((data) => {
      if (data.length != 0) {
          createProfile(data);
          displayForm(data[0].userType);
          document.getElementById("profile-section").style.display = "block";
          document.getElementById("no-profile").style.display = "none";
      } else {
          document.getElementById("profile-section").style.display = "none";
          document.getElementById("no-profile").style.display = "block";
      }

  });
}

function createProfile(data) {
    const emailFormContainer = document.getElementById("email-form");
    emailFormContainer.value = data[0].email;
    document.getElementById("email-form-display").innerText = data[0].email;

    const idFormContainer = document.getElementById("datastore-id");
    idFormContainer.value = data[0].datastoreId;

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

}

// function to toggle between the two different forms
function displayForm(userType) {
  if (userType == "individual") {
    document.getElementById("user").style.display = "block";
    document.getElementById("organization").style.display = "none";
    document.getElementById("user-type-toggle").value = "individual";

  } else if (userType == "organization") {
    document.getElementById("user").style.display = "none";
    document.getElementById("organization").style.display = "block";
    document.getElementById("org-user-type").value = "organization";
  }
}


function createDivElement(event) {
  const divElement = document.createElement('div');
  divElement.setAttribute("class", "item-container");
 
  const h3ElementName = document.createElement('h3');
  h3ElementName.innerText = event;
  divElement.appendChild(h3ElementName);

}

// function used to toggle after a change in the selected user type input
function toggleForm(formUserType) {
  var userType = document.getElementById(formUserType).value;
  displayForm(userType);
}