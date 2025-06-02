## This is a Spring Boot application with maven as dependency manager
----------------------
#### Please check database setting in 
- src/main/resources/application.properties.

#### The default database name for this application is comp47360
--------------------
### To start the application in development mode, please run
- mvn clean package
- mvn spring-boot:run
-------------------------
### Check API by starting this application and open
### http://localhost:8080/swagger-ui/index.html
---------------

[//]: # (### Login api is POST request to http://localhost:8080/login )

[//]: # (### with form data: )

[//]: # (- #### **"username"** as key, **email** as value )

[//]: # (- #### **"password"** as key, password as value)