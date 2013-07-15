-- basic schema

# --- !Ups

CREATE TABLE stories (
  id serial NOT NULL PRIMARY KEY,
  title varchar(100) NOT NULL,
  url varchar(2000) NOT NULL,
  user_id int NOT NULL,
  score int NOT NULL,
  created_at timestamp NOT NULL DEFAULT current_timestamp
);

CREATE TABLE users (
  id serial NOT NULL PRIMARY KEY,
  username varchar(20) NOT NULL UNIQUE,
  password varchar(60) NOT NULL,
  pw_hash varchar(20) NOT NULL,
  pw_salt varchar(20),
  firstname varchar(100) NOT NULL,
  lastname varchar(100) NOT NULL,
  email varchar(100) NOT NULL UNIQUE,
  created_at timestamp NOT NULL DEFAULT current_timestamp
);

CREATE TABLE tokens (
  id serial NOT NULL PRIMARY KEY,
  token char(36) NOT NULL UNIQUE,
  email varchar(100) NOT NULL,
  creation timestamp NOT NULL,
  expiration timestamp NOT NULL,
  signed_up boolean NOT NULL,
  created_at timestamp NOT NULL DEFAULT current_timestamp
);

# --- !Downs

DROP TABLE stories;
DROP TABLE users;
DROP TABLE tokens;