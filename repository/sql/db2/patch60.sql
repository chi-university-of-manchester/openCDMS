-- Patch to add query engine functionality
-- Required for all deployments prior to r11583

alter table t_export_requests add c_query_id bigint;
create table t_date_statements (c_id bigint not null, c_value timestamp, primary key (c_id));
create table t_entry_statements (c_id bigint not null, c_operator varchar(255), c_entry_id bigint not null, c_doc_occ_id bigint not null, primary key (c_id));
create table t_int_statements (c_id bigint not null, c_value integer, primary key (c_id));
create table t_num_statements (c_id bigint not null, c_value double, primary key (c_id));
create table t_opt_statements (c_id bigint not null, c_option_id bigint, primary key (c_id));
create table t_queries (c_id bigint not null, c_dataset_id bigint not null, c_name varchar(255), c_description varchar(1024), c_owner varchar(255), c_operator varchar(255), c_public smallint, primary key (c_id));
create table t_query_groups (c_query_id bigint not null, c_group_id bigint not null, c_index integer not null, primary key (c_query_id, c_index));
create table t_statements (c_id bigint not null, c_query_id bigint not null, c_index integer, primary key (c_id));
alter table t_date_statements add constraint FK415A604A8631341E foreign key (c_id) references t_entry_statements;
alter table t_entry_statements add constraint FK1D3CF3C7AA2CD1C foreign key (c_id) references t_statements;
alter table t_entry_statements add constraint FK1D3CF3C9C24716D foreign key (c_entry_id) references t_entrys;
alter table t_entry_statements add constraint FK1D3CF3CD307CAA3 foreign key (c_doc_occ_id) references t_doc_occs;
alter table t_int_statements add constraint FK5F54BE5F8631341E foreign key (c_id) references t_entry_statements;
alter table t_num_statements add constraint FK186356688631341E foreign key (c_id) references t_entry_statements;
alter table t_opt_statements add constraint FK878D851B8631341E foreign key (c_id) references t_entry_statements;
alter table t_opt_statements add constraint FK878D851B84231307 foreign key (c_option_id) references t_options;
alter table t_queries add constraint FKE29DCA9B49CB0AD7 foreign key (c_id) references t_persistents;
alter table t_queries add constraint FKE29DCA9BD0BFC2CD foreign key (c_dataset_id) references t_datasets;
alter table t_query_groups add constraint FK259D931655F3894C foreign key (c_query_id) references t_queries;
alter table t_query_groups add constraint FK259D9316373B9A4D foreign key (c_group_id) references t_groups;
alter table t_statements add constraint FKB08B498F49CB0AD7 foreign key (c_id) references t_persistents;
alter table t_statements add constraint FKB08B498F55F3894C foreign key (c_query_id) references t_queries;
