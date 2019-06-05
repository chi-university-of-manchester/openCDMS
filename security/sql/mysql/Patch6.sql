alter table t_users add column c_passwd_reset_date datetime; 
alter table t_users add column c_passwd_reset_uuid varchar(36);
create index user_uuid_index on t_users (c_passwd_reset_uuid);
