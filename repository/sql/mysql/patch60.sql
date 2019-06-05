-- Patch to add query engine functionality
-- Required for all deployments prior to r11583

alter table t_export_requests add c_query_id bigint;
create table t_date_statements (c_id bigint not null, c_value datetime, primary key (c_id)) type=InnoDB;
create table t_entry_statements (c_id bigint not null, c_operator varchar(255), c_entry_id bigint not null, c_doc_occ_id bigint not null, primary key (c_id)) type=InnoDB;
create table t_int_statements (c_id bigint not null, c_value integer, primary key (c_id)) type=InnoDB;
create table t_num_statements (c_id bigint not null, c_value double precision, primary key (c_id)) type=InnoDB;
create table t_opt_statements (c_id bigint not null, c_option_id bigint, primary key (c_id)) type=InnoDB;
create table t_queries (c_id bigint not null, c_dataset_id bigint not null, c_name varchar(255), c_description text, c_owner varchar(255), c_operator varchar(255), c_public bit, primary key (c_id)) type=InnoDB;
create table t_query_groups (c_query_id bigint not null, c_group_id bigint not null, c_index integer not null, primary key (c_query_id, c_index)) type=InnoDB;
create table t_statements (c_id bigint not null, c_query_id bigint not null, c_index integer, primary key (c_id)) type=InnoDB;
alter table t_date_statements add index FK415A604A8631341E (c_id), add constraint FK415A604A8631341E foreign key (c_id) references t_entry_statements (c_id);
alter table t_entry_statements add index FK1D3CF3C7AA2CD1C (c_id), add constraint FK1D3CF3C7AA2CD1C foreign key (c_id) references t_statements (c_id);
alter table t_entry_statements add index FK1D3CF3C9C24716D (c_entry_id), add constraint FK1D3CF3C9C24716D foreign key (c_entry_id) references t_entrys (c_id);
alter table t_entry_statements add index FK1D3CF3CD307CAA3 (c_doc_occ_id), add constraint FK1D3CF3CD307CAA3 foreign key (c_doc_occ_id) references t_doc_occs (c_id);
alter table t_int_statements add index FK5F54BE5F8631341E (c_id), add constraint FK5F54BE5F8631341E foreign key (c_id) references t_entry_statements (c_id);
alter table t_num_statements add index FK186356688631341E (c_id), add constraint FK186356688631341E foreign key (c_id) references t_entry_statements (c_id);
alter table t_opt_statements add index FK878D851B8631341E (c_id), add constraint FK878D851B8631341E foreign key (c_id) references t_entry_statements (c_id);
alter table t_opt_statements add index FK878D851B84231307 (c_option_id), add constraint FK878D851B84231307 foreign key (c_option_id) references t_options (c_id);
alter table t_queries add index FKE29DCA9B49CB0AD7 (c_id), add constraint FKE29DCA9B49CB0AD7 foreign key (c_id) references t_persistents (c_id);
alter table t_queries add index FKE29DCA9BD0BFC2CD (c_dataset_id), add constraint FKE29DCA9BD0BFC2CD foreign key (c_dataset_id) references t_datasets (c_id);
alter table t_query_groups add index FK259D931655F3894C (c_query_id), add constraint FK259D931655F3894C foreign key (c_query_id) references t_queries (c_id);
alter table t_query_groups add index FK259D9316373B9A4D (c_group_id), add constraint FK259D9316373B9A4D foreign key (c_group_id) references t_groups (c_id);
alter table t_statements add index FKB08B498F49CB0AD7 (c_id), add constraint FKB08B498F49CB0AD7 foreign key (c_id) references t_persistents (c_id);
alter table t_statements add index FKB08B498F55F3894C (c_query_id), add constraint FKB08B498F55F3894C foreign key (c_query_id) references t_queries (c_id);
