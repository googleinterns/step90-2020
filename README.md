# Activity Hall

The goal of this project is to create a platform that allows college students to find events that organizations they follow are currently hosting. The project allows users to find events on campus based on topics/organizations that they are interested in. This solves the frustration of searching for events on campus and includes monetary constraints to limit results to the users’ needs and by strengthening their connection to their community . This project is for everyone in college! Anyone who is looking for something to do on a boring saturday night by yourself or with friends will find this useful. We want to address the restrictions and considerations that different groups of people face, and make it so that these aspects are integrated into the search for university community-based events.

## Running Locally

This will run with local authentication, skipping testing:

```
mvn clean spring-boot:run -P local -DskipTests
```

Test user credentials are set in `pom.xml`:
* Username: `student@uni.org`
* Password: `test`

## Running on App Engine

This project is deployed at https://step90-2020.ue.r.appspot.com/ for a limited amount of time and only available to Google employees with a @google.com account.

To deploy, set up a GCP project, and run the following command:

```
mvn package appengine:deploy -Dapp.deploy.version=[your-version]
```

## Using Activity Hall

### Creating Individual User Account and Manage Individual User Account

![alt text](src/main/resources/static/images/organizationuser.gif)
