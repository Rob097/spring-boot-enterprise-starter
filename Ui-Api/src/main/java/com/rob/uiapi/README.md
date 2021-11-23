# Ui-Api Module
This is the wiki of the Ui-Api module. This module contains all the elements used from the BE to communicate properly with the FE and with the external world in general thanks to CRUD calls.

## Utils
In the utils folder there are a huge amount of classes and methods extremely useful used in this module. For example it contains all the interfaces used by controllers and mappers and views but also the cookieHelper and SortHelper and all the classes used to encapsulate some messages that we want to send to the FE along the response content itself.

## DTOs
The dto folder is divided in two macro categories: **Modules** and **Mappers**.
* The **Modules** are the modules that represent the class for the FE side. In fact, as a convention, we don't want to use the same module to work in the BE and also to present the information to the FE so, we create a second class for each kind of object but only with the information that we actually want to send to the FE or in general to the caller. In order to pass from a DTO to a _value object_ (classes of BE) or vice versa, we use the mapper.
* The **Mappers** are special classes used to create a DTO from a VO or to create a VO from a DTO. As you can see there are two kinds of mappers: **Mappers** and **RMappers**. The Mappers are used to transform a DTO into a brand-new VO while a RMapper is used to trasform a VO into a DTO.

## Controllers
The controllers represent the RestServices, and they all work the same, except for the Authentication controller AuthRS. This rest service is different because it doesn't implement the UIApiRS interface, and it's also a unique behaviour for the authentication and registration of users. In fact it cooperates with the Sec-Auth-conf module for security and jwt authentication.
All the other RS works the same as long as they implement the **interface UIApiRS**. This interface requires to implement some methods to embrace all the CRUD aspects. 
The methods are: 
* **find**: is a GET request that returns a collection of elements
* **get**: is a GET request that returns a single element
* **update**: is a PUT request used to update some DB data
* **create**: is a POST request used to insert new DB data
* **deleteAll**: is a DELETE request used to remove a collection of elements
* **delete**: is a DELETE request used to remove a single element

The RS controllers have some very important elements:
* Range: the range is a class used to communicate to the PrepareStaementBuilder how to build the query in order to extrapolate only a particular range of results. For example, you may want only the first 20 elements or maybe the elements from the fifth to the tenth. By default, is setted a range that returns a maximum number of 20 results.
* SearchCriteria: The searchCriteria classes are a particular type of classes created from the parameters send with the crud request in the method applySearchCriteria in order to add some filters to the query.
* Fetch. Those are a unique and extremely useful type of class. The fetch is applied from the view. When we send a crud request, we also could send a view attribute that says how many details we want to receive in our response. If we send a synthetic view, we'll have fewer details than if we send a normal or verbose view. They work along with FetchBuilder and FetchHandler.
