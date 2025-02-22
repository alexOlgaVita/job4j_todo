create table tasks
(
   id SERIAL PRIMARY KEY,
   name VARCHAR not null,
   description VARCHAR not null,
   created TIMESTAMP,
   done BOOLEAN
);