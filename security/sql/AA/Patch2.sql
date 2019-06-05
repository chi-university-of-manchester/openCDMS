alter table t_login_record add c_authenticated smallint not null;
update t_login_record set c_authenticated=1;