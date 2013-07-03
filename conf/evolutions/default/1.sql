# stories schema

# --- !Ups

CREATE TABLE stories (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    title varchar(100) NOT NULL,
    url varchar(2000) NOT NULL,
    score int(10) NOT NULL,
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE stories;
