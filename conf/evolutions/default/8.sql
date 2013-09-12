-- add welcome widget to system user

# --- !Ups

insert into widgets (user_id, type, created_at)
values ((select id from users where username='system'), 3, now());

insert into widget_layout (widget_id, col, pos, created_at)
values ((select id from widgets where type = 3), 0, 0, now());

# --- !Downs

delete from widget_layout where widget_id in (select id from widgets where type = 3);
delete from widgets where type = 3;