CREATE KEYSPACE IF NOT EXISTS portal WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};
 use portal;
 drop table if exists user;
create table user(email text PRIMARY KEY,password text,category text);
