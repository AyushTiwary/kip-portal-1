# kip-portal

### How to run User-Api module :

Pre-requisite :  Running cassandra

Below steps are required to run user-api module

1) Download and extract Cassandra
2) start cassandra as service go to bin folder and execute command ./cassandra 

   3. run following command to ingest schema in cassandra

    inside bin folder of downloaded casandra
    
    ./cqlsh -f <kip project path/backend/common/src/main/resources/cassandra_script.cql>

4) export $USER_EMAIL=email from which you want to send mail 
    and $USER_PASSWORD=password 
5) start kip server with this command
   sbt "project user-api" run 
   port for all routes is 8080
6) kip server will start on localhost:8080
 
### Kip Routes
  ```
  Login Route
  http://localhost:8080/kip/login 
  
  Create user Route
  http://localhost:8080/kip/createuser
   ```
### Sample Json
   ```
   Json for creating new user
   
   {
     "emailId": "anubhav.tarar@knoldus.in" 
   }
   
   Json for user login
   
   {
     "emailId": "anubhav.tarar@knoldus.in",
     "password": "iql2ihrdouauv4ps9q9t57i2"
   
   }
   ```