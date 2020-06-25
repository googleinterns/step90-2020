
/** checks whether the user is authenticated and adjust elements 
according to whether the user is logged in or logged out */
function checkAuth(redirect){
  // send request for information on login status
  fetch('authenticate?redirect=' + redirect).then(response => response.json()).then((data) => {
    const commentDivElement = document.getElementById('auth-container');
    const hElement = document.createElement('h1');
    const navElement = document.getElementById("nav");
    const liElement = document.createElement('li');
    const aElement = document.createElement("a");
    
    // adjust visibility and login/logout button according to 
    // whether user is logged in or not
    if (data["user"] != "Stranger") {
      hElement.innerHTML = "Hello " + data["user"];
      aElement.href = data["url"];
      aElement.innerText = "Logout";
      liElement.appendChild(aElement);
      
    
    } else {
      hElement.innerHTML = "Hello! Please login";
      aElement.href = data["url"];
      aElement.innerText = "Login";
      liElement.appendChild(aElement);
    }
    commentDivElement.appendChild(hElement);
    navElement.appendChild(liElement);
    
  });
}

function getUser() {
    fetch('get-user?email=' + 'js112@princeton.edu').then(response => response.json()).then((data) => {
    const firstNameContainer = document.getElementById("firstname");
    const pElementFirstName = document.createElement('p');
    pElementFirstName.innerText = "First Name: " + data.firstName;
    firstNameContainer.appendChild(pElementFirstName);

    const lastNameContainer = document.getElementById("lastname");
    const pElementLastName = document.createElement('p');
    pElementLastName.innerText = "Last Name: " + data.lastName;
    lastNameContainer.appendChild(pElementLastName);

    const emailContainer = document.getElementById("email");
    const pElementEmail = document.createElement('p');
    pElementEmail.innerText = "Email: " + data.email;
    emailContainer.appendChild(pElementEmail);

    const userTypeContainer = document.getElementById("user-type");
    const pElementUserType = document.createElement('p');
    pElementUserType.innerText = "User Type: " + data.userType;
    userTypeContainer.appendChild(pElementUserType);

    const universityContainer = document.getElementById("university");
    const pElementUniversity = document.createElement('p');
    pElementUniversity.innerText = "University: " + data.university;
    universityContainer.appendChild(pElementUniversity);

    const descriptionContainer = document.getElementById("description");
    const pElementDescription = document.createElement('p');
    pElementDescription.innerText = "Bio: " + data.description;
    descriptionContainer.appendChild(pElementDescription);

    // const savedEventsContainer = document.getElementById("saved-events");
    // savedEventsContainer.innerHTML = '';
    // for (var i = 0; i < data.savedEvents.length; i++) {
    //   savedEventsContainer.appendChild(
    //   createDivElement(data.savedEvents[i]));
    // }
  });
}

// function createDivElement(event) {
//   const divElement = document.createElement('div');
//   divElement.setAttribute("class", "item-container");
 
//   const h3ElementName = document.createElement('h3');
//   h3ElementName.innerText = event;
//   divElement.appendChild(h3ElementName);

// }