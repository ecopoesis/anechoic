-- constrain widet_layout

# --- !Ups

alter table widget_layout ADD CONSTRAINT widget_id_unique UNIQUE (widget_id);

# --- !Downs

alter table widget_layout DROP CONSTRAINT widget_id_unique;