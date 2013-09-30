-- verified email

# --- !Ups

ALTER TABLE users ADD COLUMN verified_email bit(1) NOT NULL DEFAULT '1';

# --- !Downs

ALTER TABLE users DROP COLUMN verified_email;