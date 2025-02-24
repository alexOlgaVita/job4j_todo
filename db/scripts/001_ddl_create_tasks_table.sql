create table tasks
(
   id SERIAL PRIMARY KEY,
   name VARCHAR not null,
   description VARCHAR not null,
   created TIMESTAMP not null,
   done BOOLEAN,
   CONSTRAINT name_check CHECK(name != '')
);