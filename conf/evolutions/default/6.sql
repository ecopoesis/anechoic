-- widget layout

# --- !Ups

CREATE TABLE widget_layout (
  id serial NOT NULL PRIMARY KEY,
  widget_id bigint NOT NULL,
  col int NOT NULL,
  pos int NOT NULL,
  created_at timestamp NOT NULL DEFAULT current_timestamp
);

create index widget_layout_widget on widget_layout (widget_id);

# --- !Downs

DROP TABLE widget_layout;