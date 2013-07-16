-- basic schema

# --- !Ups

create index stories_user on stories (user_id);

CREATE TABLE comments (
  id serial NOT NULL PRIMARY KEY,
  user_id bigint NOT NULL,
  story_id bigint NOT NULL,
  parent_id bigint NULL,
  comment text NOT NULL,
  score int NOT NULL,
  created_at timestamp NOT NULL DEFAULT current_timestamp
);

create index comments_user on comments (user_id);
create index comments_story on comments (story_id);

# --- !Downs

DROP TABLE comments;