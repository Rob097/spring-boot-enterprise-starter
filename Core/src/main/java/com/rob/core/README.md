# Properties, Utils and Transactions

Hi, this is a sum-up file of utils folder of core module 📖


## Properties

The properties class is divided in two macro uses. 
1. The first one is by calling the default constructor. With this approach you'll have at your disposal all the constants declared in the class and nothing else.
2. The second one is more interesting and in order to use it you'll have to instantiate the class by its custom constructor.
  Passing a string in the constructor, the Properties class will automatically search in resources folder of Sec-Auth-Conf module the .properties file called as the parameter.
  With this approach you'll have at your disposal all the constants and also all the properties defined into the .properties that you chose to load.
 
## Utils
All the files in the Utils folder are classed that contains a lot of methods to simplify coding complex functions. For instance regarding dates or null pointer ecc...

## Transactions
The transactions are a core functionality and it's build to be very customizable.
I now try to summarize the process that is innested when a User send from FE a GET request to BE which'll try to interrogate the DB and at last it will response to FE.

When a User send a request to our BE, a lot of things will happend. First of all, our configuration in WebSecurityConfig and our filter in AuthTokenFilter will be called and they
will check if the request needs to be authenticated (an authentication request obviously wont need to be authenticated) and if it does they will check the the JWT token is valid.
The RestService called will be triggered and it will check that the parameters, consumes, ecc are presents, valid and also well formed.
At this point we have two different situations: the GET and DELETE requests and the POST and PUT requests.

### GET and DELETE requests
These two type of requests have an equal behaviour so let's explain just the GET to semplify.
When a GET request arrives at BE the RS will check the validity of the path parameters.
A *SearchCriteria* will be created using the applySearchCriteria method and also the *FETCH* are applyed by applyViewCriteria method.
Inside our RS we have and @Autowired of the repository or service used.
With GET requests we usually don't need the services while sometimes with DELETE requests we do.
At this poin the method *findById* or *findByCriteria* of our repository will be called and inside this method starts our **Transaction** process that I'll explain in a bit.
The query will be created with our SearchCriteria in a *ManagerQuery* class and executed in the repository. The object will be instantiated with the resultSet and also the FETCH
will be called.
The RS will receive the results and, at this point, a *RMapper* will be called in order to map our ValueObject class (BE objects) in a DTO class (FE objects).
To finish the process, the DTO will be send to FE incapsulated into a ResponseEntity<MessageResources<*>>.

### POST and PUT requests
These two types of requests work really similar to the other two but they have some different points.
The first thing is that our RS will receive not just some path parameters but also a body.
An R-Entiy (DTO) will be formed by the body and so we'll have to call a *Mapper* to map our DTO into a valid ValueObject.
The second thing is that often we wont go directly into the repository but instead from the RS we'll call a service where we'll build all our business logic.
Inside here we'll call our repository and the next passages are the same as for the GET.

### DB Transactions
Building our application, a listener will be triggered: PortfolioContextListener. This listener will valorize the connectionPool creating a custom dataSource inside _SqlDataSource.getDataSource()_.

When we are in our repository we create a special class called **PreparedStatementBuilder** which we instantiate inside our ManagerQuery. This class is not a simple String Builder that concatenates strings, is also a fundamental part of our transaction process. This class in fact bind dynamically variables creating an INLINE query.

The PSB class implements AutoCloseable so, in our repository, we have to put our PSB inside a try catch in the () of the try in order to close the borrowed connection after the transaction's complete.

The class used for our Transaction Process are:
* [PortfolioContextListener](https://github.com/Rob097/MyPortfolio-Backend/blob/ace0edb8f321153c1cb10d05e3d6bd45dde6abb4/Core/src/main/java/com/rob/core/utils/java/PortfolioContextListener.java): Custom Listener to init the connection Pool
* [ContextListener](https://github.com/Rob097/MyPortfolio-Backend/blob/ace0edb8f321153c1cb10d05e3d6bd45dde6abb4/Core/src/main/java/com/rob/core/utils/java/ContextListener.java) Listener extended by PortfolioContextListener and called thanks to the Bean defined in the module Sec-Auth-Conf [WebConfig](https://github.com/Rob097/MyPortfolio-Backend/blob/ace0edb8f321153c1cb10d05e3d6bd45dde6abb4/Sec-Auth-Conf/src/main/java/com/rob/security/configuration/WebConfig.java)
* [SessionObject](https://github.com/Rob097/MyPortfolio-Backend/blob/ace0edb8f321153c1cb10d05e3d6bd45dde6abb4/Core/src/main/java/com/rob/core/utils/java/SessionObject.java): Extended by PreparedStatementBuilder
* [PreparedStatementBuilder](https://github.com/Rob097/MyPortfolio-Backend/blob/ace0edb8f321153c1cb10d05e3d6bd45dde6abb4/Core/src/main/java/com/rob/core/utils/db/PreparedStatementBuilder.java): Special Class to build dynamic query. It calls all the commands to open the connection, execute the query, close the connection.
* [SqlDataSource](https://github.com/Rob097/MyPortfolio-Backend/blob/ace0edb8f321153c1cb10d05e3d6bd45dde6abb4/Core/src/main/java/com/rob/core/utils/db/SqlDataSource.java): Init the connectionPool and contains the singletone instnce of the custom SqlDataSource
* [SqlConnection](https://github.com/Rob097/MyPortfolio-Backend/blob/ace0edb8f321153c1cb10d05e3d6bd45dde6abb4/Core/src/main/java/com/rob/core/utils/db/SqlConnection.java): Represents the custom class of the connection to the DB.

## Services
The service contains all the business logic of the project. They can call other services or repositories.

## Repositories
The repositories are used to link the BE to the DB. They just create the PSB and they initialize the connection, execute the queries, call the fetches and return the elements.

## ManagerQuery
The managerQueries are used to create the query thanks to the searchCriteria.
