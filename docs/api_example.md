### Swagger example API call
- local: http://localhost:8080/api/swagger-ui/index.html
- remote: https://planhattan.ddns.net/api/swagger-ui/index.html
---------------
### Example API with Postman
#### poi list & zone busyness
- query params & poi list:
![alt text](get_pois.png)
- zone busyness:
![alt text](get_pois2.png)
#### Recommendation API:
- POST Request
- fixed location should contain latitude, longitude, poiName, zoneId, time, transitType (optional) 
- uncertain location should contain poiTypeName, time, transitType (optional) 
- the first location should be fixed start location without transitType
![alt text](get_pois_recommendation.png)
#### POI Type API:
![alt text](get_poitypes.png)
#### Zone busyness API:
- predict one zone's busyness for continuous predictedHours after a specific time
![alt text](get_zones.png)
#### All Zone busyness API:
- predict all zones' busyness for a specific time
![alt text](get_zones_all.png)
#### Weather API:
- check https://openweathermap.org/api/one-call-3 for details
![alt text](get_weather.png)
#### Register API:
- userName and password are necessary.
- userPicture will be ignored, should be updated after registration.
- email will be linked automatically when user login through third-party account.
![alt text](post_register.png)
#### Login API:
- has to be form login
![alt text](post_login.png)
#### CSRF Token API:
- has to login first
- then get the csrf token
![alt text](get_csrf_token.png)
- then add the token to the header with ```X-CSRF-TOKEN:{token}``` for following requests that need authentication.
![alt text](get_csrf_token2.png)
![alt text](get_csrf_token3.png)
#### Update User Picture API:
- has to login first & set the csrf token in header
- file size limit is 5MB
- file type should be image/jpeg, image/png, image/gif
![alt text](post_user_picture.png)
#### Get User Picture API:
- don't need login, userPicture is public.
![alt text](get_user_picture.png)
#### Get User Details API:
- has to login first & set the csrf token in header
- you can use ```<img src={user.userPicture}>```  directly in html to display the picture
![alt text](get_user.png)
#### Update User Details API:
- has to login first & set the csrf token in header
- we don't offer email update service, it will link to third-party account automatically
- userPicture will be ignored, should be updated by posting.
![alt text](put_user.png)
#### Logout API:
- has to login first & set the csrf token in header
![alt text](post_logout.png)
#### Delete User API:
- has to login first & set the csrf token in header
![alt text](delete_user.png)
#### Add User Plan API:
- has to login first & set the csrf token in header
![alt text](post_userplans.png)
#### Add Multiple User Plan API:
- has to login first & set the csrf token in header
![alt text](post_userplans_multiple.png)
#### Get User Plan API:
- has to login first & set the csrf token in header
![alt text](get_userplans.png)
#### Update User Plan API:
- has to login first & set the csrf token in header
![alt text](put_userplans.png)
#### Delete User Plan API:
- has to login first & set the csrf token in header
![alt text](delete_userplans.png)
#### Delete Multiple User Plan API:
- has to login first & set the csrf token in header
![alt text](delete_userplans_multiple.png)