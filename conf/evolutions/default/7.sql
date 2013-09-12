-- system user

# --- !Ups

insert into users (username, password, pw_hash, pw_salt, firstname, lastname, email, created_at, scheme)
values ('system', '$2a$10$SS/woSAeEopFljQKOzEvhO/Wx2ywccjVHWS9WR.FwfP/OoPYnKWQq', 'bcrypt', null, 'System', 'Account', 'system@anechoicnews.com', now(), 'dark');

# --- !Downs

delete from users where username = 'system';