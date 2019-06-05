create table t_text_statements (c_id bigint not null, c_value varchar(255), primary key (c_id));
create table t_long_text_statements (c_id bigint not null, primary key (c_id));
alter table t_text_statements add constraint FK93FCF56B8631341E foreign key (c_id) references t_entry_statements;
alter table t_long_text_statements add constraint FK788E565E99FD9F6F foreign key (c_id) references t_text_statements;