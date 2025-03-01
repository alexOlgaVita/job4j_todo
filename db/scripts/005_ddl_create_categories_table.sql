CREATE TABLE categories (
   id serial PRIMARY KEY,
   name TEXT UNIQUE NOT NULL
);

CREATE TABLE task_category (
   id serial PRIMARY KEY,
   task_id int not null REFERENCES tasks(id),
   category_id int not null REFERENCES categories(id),
   UNIQUE (task_id, category_id)
);