# Spring Boot Starter Project

Hi! This is a starter project for enterprise spring boot web applications.

To organize my work as an Agile Company I'm using thee free plan of Atlassian Jira.

Please read this Repo's [wiki](https://github.com/Rob097/spring-boot-enterprise-starter/wiki) for more information.

Above the specific of how it works 😉

### Backend
As I said, the backend of the project is developed in Java with Spring boot.
I'm using Heroku to host an example of application developed starting from this project and the URL is https://rob-portoflio-1997.herokuapp.com/
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


### Database
For the dataBase I have two choice.
  - The first one is to use my personal Raspberry that I've properly configured to host my DB.
  - The second is to use a database that I've created on a SiteGround hosted website that I've created last year for my parent's Butchery.

Bye! 😉
