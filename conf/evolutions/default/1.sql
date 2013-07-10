-- stories table

# --- !Ups

CREATE TABLE stories (
  id serial NOT NULL PRIMARY KEY,
  title varchar(100) NOT NULL,
  url varchar(2000) NOT NULL,
  score int NOT NULL
);

# --- !Downs

DROP TABLE stories;
