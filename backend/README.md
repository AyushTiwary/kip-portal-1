KIP - Platform for kip automation


### How to run User-Api module :

Pre-requisite :  Running cassandra

Below steps are required to run user-api module

1) Download and extract Cassandra
2) start cassandra as service go to bin folder and execute command ./cassandra 
3) start cqlsh by entering command ./cqlsh inside folder
4) export $USER_EMAIL=email from which you want to send mail 
    and $USER_PASSWORD=pasword of user
5) start kip server with this command
   sbt "project user-api" run 
   port for all routes is 8080

 