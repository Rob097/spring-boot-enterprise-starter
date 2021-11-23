# Angular & Spring Boot Project

Hi! This is My personal portfolio webApp. This is the backend developed in Spring boot Java.

To organize my work as an Agile Company I'm using thee free plan of Atlassian Jira (https://rob-portfolio.atlassian.net).

Please read this Repo's [wiki](https://github.com/Rob097/MyPortfolio-Backend/wiki) for more information.

Above the specific of how it works 😉

### Backend
As I said, the backend of the project is developed in Java with Spring boot.
I'm using Heroku to host my application and the URL is https://rob-portoflio-1997.herokuapp.com/
How does it work?

Well, this project is a Multi-Module project. This means that I've divided the main functionalities in different modules.
There is the Main module that contains the Main class. This module is a must because in order to execute a multi-module project
you need to have a module that includes all the others. And the better way to do that is to create a single module which unique goal is
to execute the project.

There is a module for Authentication, Security, and Configurations. For authentication and security I've used the Spring Security dependency along side JWT. In this module there is the class that identify the users and also the relative DTOs (UserR...).
We also use this module to contain all the configurations and constants and sharable-among-modules parts.
Then we have other two modules: Core and Ui-Api.
The Ui-Api is the module that contains all the object that "talk" with the frontEnd such as Rest services, dto and mappers ecc.
The Core is used for everything else. ValueObjects, Services for logic, repositories and managerquery for database interrogations.

### Frontend
The frontend is developed using Angular 12.
I could have used Heroku also for the frontend, integrating with npm but, just to have a wider range of tools I decided to
use Firebase and the URL is https://myportfolio-6a771.web.app/


### Database
For the dataBase I have two choice.
  - The first one is to use my personal Raspberry that I've properly configured to host my DB (http://ourlists.ddns.net:3306/myprofile)
  - The second is to use a database that I've created on a SiteGround hosted website that I've created last year for my parent's Butchery
  	(https://macelleriadellantonio.it:3306/dbvgv0cyhhsbb0)


Bye! 😉
