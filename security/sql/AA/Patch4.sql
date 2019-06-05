alter table t_users add c_dormant smallint not null;
update t_users set c_dormant=0;
