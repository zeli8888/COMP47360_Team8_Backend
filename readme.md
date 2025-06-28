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
### http://localhost:8080/api/swagger-ui/index.html
### The frontend url is set to http://localhost:3000, run frontend in this url to avoid cross-origin issue
---------------
### Example API with Postman
#### poi list & zone busyness
- query params & poi list:
![alt text](docs/get_pois.png)
- request body & zone busyness:
![alt text](docs/get_pois2.png)
#### Recommendation API:
- fixed location should contain latitude, longitude, poiName, zoneId, time, transitType (optional) 
- uncertain location should contain poiTypeName, time, transitType (optional) 
- the first location should be fixed start location without transitType
![alt text](docs/get_pois_recommendation.png)
#### POI Type API:
![alt text](docs/get_poitypes.png)
#### Zone busyness API:
- predict one zone's busyness for continuous predictedHours after a specific time
![alt text](docs/get_zones.png)
#### Weather API:
- check https://openweathermap.org/api/one-call-3 for details
![alt text](docs/get_weather.png)
#### Register API:
- userName and password are necessary.
- userPicture will be ignored, should be updated after registration.
- email will be linked automatically when user login through third-party account.
![alt text](docs/post_register.png)
#### Login API:
- has to be form login
![alt text](docs/post_login.png)
#### CSRF Token API:
- has to login first
- then get the csrf token
![alt text](docs/get_csrf_token.png)
- then add the token to the header with ```X-CSRF-TOKEN:{token}``` for following requests that need authentication.
![alt text](docs/get_csrf_token2.png)
![alt text](docs/get_csrf_token3.png)
#### Update User Picture API:
- has to login first & set the csrf token in header
- file size limit is 5MB
- file type should be image/jpeg, image/png, image/gif
![alt text](docs/post_user_picture.png)
#### Get User Picture API:
- don't need login, userPicture is public.
![alt text](docs/get_user_picture.png)
#### Get User Details API:
- has to login first & set the csrf token in header
- you can use ```<img src={user.userPicture}>```  directly in html to display the picture
![alt text](docs/get_user.png)
#### Update User Details API:
- has to login first & set the csrf token in header
- we don't offer email update service, it will link to third-party account automatically
- userPicture will be ignored, should be updated by posting.
![alt text](docs/put_user.png)
#### Logout API:
- has to login first & set the csrf token in header
![alt text](docs/post_logout.png)
#### Delete User API:
- has to login first & set the csrf token in header
![alt text](docs/delete_user.png)
#### Add User Plan API:
- has to login first & set the csrf token in header
![alt text](docs/post_userplans.png)
#### Get User Plan API:
- has to login first & set the csrf token in header
![alt text](docs/get_userplans.png)
#### Update User Plan API:
- has to login first & set the csrf token in header
![alt text](docs/put_userplans.png)
#### Delete User Plan API:
- has to login first & set the csrf token in header
![alt text](docs/delete_userplans.png)

---------------------
### Testing
- Please check the testing folder src/test/java/team8/comp47360_team8_backend
- For unit testing, there are test cases for controller, service
- If you want to contribute, please create a new branch and push to github. After you finish, please message me to avoid duplicate work.
- I have done POIControllerTest, you can check for reference.
- We will implement integration testing in the future.
- This is not mandatory, so only do it when you have free time.