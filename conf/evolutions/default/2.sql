-- users table

# --- !Ups

CREATE TABLE users (
    id serial NOT NULL PRIMARY KEY,
    username varchar(20) NOT NULL UNIQUE,
    password varchar(60) NOT NULL,
    pw_hash varchar(20) NOT NULL,
    pw_salt varchar(20) NOT NULL,
    firstname varchar(100) NOT NULL,
    lastname varchar(100) NOT NULL,
    email varchar(100) NOT NULL UNIQUE
);

# --- !Downs

DROP TABLE users;