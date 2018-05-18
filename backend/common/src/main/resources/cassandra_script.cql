Drop keyspace portal;
CREATE KEYSPACE IF NOT EXISTS portal WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};
 use portal;
 drop table if exists user;
create table user(email text PRIMARY KEY,password text,category text);

CREATE TABLE portal.knolsession (
   date text PRIMARY KEY,
   numberofdays int,
   sessionid text
);

CREATE TABLE portal.sessiondate (
   date text PRIMARY KEY
);
CREATE TABLE portal.holiday (
   date text PRIMARY KEY,
   holiday text
);

CREATE TABLE portal.schedule (
   sessionid text PRIMARY KEY,
   assistanttrainer text,
   content text,
   date text,
   numberOfDays int,
   technologyname text,
   trainee text
);
