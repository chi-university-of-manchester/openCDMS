-- Patch to upgrade DB2 database from R6 to R6.1
alter table t_datasets add c_no_review smallint;
update t_datasets set c_no_review=0;
create table t_variable_defaults (c_default_id bigint not null, c_default_value_id bigint not null, c_variable_name varchar(255) not null, primary key (c_default_id, c_variable_name));
create table t_trans_req_vars (c_ede_id bigint not null, c_variable varchar(255), c_index integer not null, primary key (c_ede_id, c_index));
alter table t_variable_defaults add constraint FK207D91AAC3F16930 foreign key (c_default_value_id) references t_numeric_values;
alter table t_variable_defaults add constraint FK207D91AAD5A43913 foreign key (c_default_id) references t_derived_entrys;
alter table t_trans_req_vars add constraint FKD388AE8C8736C890 foreign key (c_ede_id) references t_external_derived_entries;