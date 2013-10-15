-- verified email

# --- !Ups

ALTER TABLE widget_properties ADD COLUMN iv varchar(100) NULL;

# --- !Downs

ALTER TABLE widget_properties DROP COLUMN iv;