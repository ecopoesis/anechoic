-- stories table

-- --- !Ups

CREATE TABLE stories (
    id serial NOT NULL,
    title varchar(100) NOT NULL,
    url varchar(2000) NOT NULL,
    score int NOT NULL,
    PRIMARY KEY (id)
);

-- --- !Downs

DROP TABLE stories;
