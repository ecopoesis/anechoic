-- widgets schema

# --- !Ups

CREATE TABLE widgets (
  id serial NOT NULL PRIMARY KEY,
  user_id bigint NOT NULL,
  type int NOT NULL,
  created_at timestamp NOT NULL DEFAULT current_timestamp
);

create index widgets_user on widgets (user_id);

CREATE TABLE widget_properties (
  id serial NOT NULL PRIMARY KEY,
  widget_id bigint NOT NULL,
  k varchar(100) NOT NULL,
  v varchar(100) NOT NULL,
  created_at timestamp NOT NULL DEFAULT current_timestamp
);

create index widget_properties_widget on widget_properties (widget_id);

# --- !Downs

DROP TABLE widgets;
DROP TABLE widget_properties;