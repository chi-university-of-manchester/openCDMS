-- Patch to add the t_change_history table
create table t_change_history (c_id bigint not null, c_user varchar(255), c_parentid bigint, c_when timestamp, c_action varchar(255), c_instance_id bigint not null, c_index integer, primary key (c_id));
alter table t_change_history add constraint FK3D74E63049CB0AD7 foreign key (c_id) references t_persistents;
alter table t_change_history add constraint FK3D74E630A166C9F8 foreign key (c_instance_id) references t_statused_instances;

alter table t_provenance add c_parent_change bigint;
alter table t_provenance add constraint FKC34F46CC4BF95664 foreign key (c_parent_change) references t_change_history;
