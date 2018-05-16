KIP - Platform for kip automation


### How to run User-Api module :

Pre-requisite :  Running cassandra

Below steps are required to run user-api module

1) Download and extract Cassandra
2) start cassandra as service go to bin folder and execute command ./cassandra 
3) start cqlsh by entering command ./cqlsh inside folder
4)create table user using csqlsh cli
  create table user(email text PRIMARY KEY,password text,category text);
5) export $USER_EMAIL=email from which you want to send mail 
    and $USER_PASSWORD=password of user
6) start kip server with this command
   sbt "project user-api" run 
   port for all routes is 8080
7) kip server will start on localhost:8080
 
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