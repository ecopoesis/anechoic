-- users and tokens table

# --- !Ups

CREATE TABLE users (
  id serial NOT NULL PRIMARY KEY,
  username varchar(20) NOT NULL UNIQUE,
  password varchar(60) NOT NULL,
  pw_hash varchar(20) NOT NULL,
  pw_salt varchar(20),
  firstname varchar(100) NOT NULL,
  lastname varchar(100) NOT NULL,
  email varchar(100) NOT NULL UNIQUE
);

CREATE TABLE tokens (
  id serial NOT NULL PRIMARY KEY,
  token char(36) NOT NULL UNIQUE,
  email varchar(100) NOT NULL,
  creation timestamp NOT NULL,
  expiration timestamp NOT NULL,
  signed_up boolean NOT NULL
);

# --- !Downs

DROP TABLE users;
DROP TABLE tokens;