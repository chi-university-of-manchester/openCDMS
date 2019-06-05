create table t_authorities (c_id bigint not null, c_action_name varchar(255), c_policy_id bigint not null, c_index integer, primary key (c_id));
alter table t_authorities add constraint FK195A63D6AAFB1BCE foreign key (c_policy_id) references t_policies;
alter table t_authorities add constraint FK195A63D6F0135D3E foreign key (c_id) references t_persistents;
