-- add color scheme column

# --- !Ups

ALTER TABLE users ADD COLUMN scheme varchar(20) NOT NULL DEFAULT 'dark';

# --- !Downs

ALTER TABLE users DROP COLUMN scheme;