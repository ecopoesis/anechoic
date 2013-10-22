-- user source

# --- !Ups

ALTER TABLE users ADD COLUMN source varchar(200) NULL;

# --- !Downs

ALTER TABLE users DROP COLUMN source;