-- audit

# --- !Ups

CREATE TABLE audit (
  id serial NOT NULL PRIMARY KEY,
  type_id int NOT NULL,
  action_id int NOT NULL,
  object_id bigint NOT NULL,
  value int NULL,
  user_id bigint NOT NULL,
  created_at timestamp NOT NULL DEFAULT current_timestamp
);

create index audit_type on audit (type_id);
create index audit_action on audit (action_id);
create index audit_user on audit (user_id);
create index audit_object on audit (object_id);

# --- !Downs

DROP TABLE audit;