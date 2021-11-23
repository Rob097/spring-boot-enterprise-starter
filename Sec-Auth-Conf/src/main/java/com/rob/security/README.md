Documentations of module Sec-Auth-Conf 📖

Just to clarify, this module's been created to have the classes of Security, authentication and also the configuration resources in a single place. 


## Security
The security part of the project is based on **Spring-security** libraries. It works with a strong connection to the authentication in fact, to every request that the FE send to the BE, there's a filter that check the authentication and also the privileges of the user, and it makes sure that the user is authorized to do that kind of operations.

## Authentication
The authentication is based on **JWT** tokens. At the moment of login, if the username and password exists, and they are correct, a jwt token is generated. If the user wants to be remembered (he checks the checkbox in the login page) the jwt token is configured to expire within a year. Although, if the user doesn't want to be remembered, the token'll expire in two hours. The token also contains some info about the user such as the username, ID, roles and, as I said, the expiration date in timestamp format. The algorithm used to encrypt the token is **HS512**. 

## Configuration
This module also contains all the configurations of the project.
A classic spring project takes all the configurations and properties from the module that contains the main class from the package resources. 
In this case however, I decided to put all the configurations in this particular module to increase the order of the project.
In order to do such thing I created an application.properties file in the resources package into the main module that redirect all the properties search in this module. Here exists, in the resources package, three properties file: The first one is **application-main.properties**. In these properties file there are a lot of general configurations such as jwt props and security. The second and the third are the two different configurations whether I decide to use the DB stored in macelleriadellantonio.it hosts or the one hosted in my raspberry at home, and respectively they are **application-mac.properties** and **application-rasp.properties**. The choice is demanded to a conf in the main properies file: spring.profiles.active. So in order to change DB configs, you have to change this property.
