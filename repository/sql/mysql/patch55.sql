create table t_esl_cust_field_values (c_esl_cust_field_id bigint not null, c_value varchar(255), c_index integer not null, primary key (c_esl_cust_field_id, c_index)) type=InnoDB;
create table t_esl_custom_fields (c_id bigint not null, c_name varchar(255), c_dataset_id bigint not null, c_index integer, primary key (c_id)) type=InnoDB;
alter table t_esl_cust_field_values add index FKDCED9067BC2A7203 (c_esl_cust_field_id), add constraint FKDCED9067BC2A7203 foreign key (c_esl_cust_field_id) references t_esl_custom_fields (c_id);
alter table t_esl_custom_fields add index FK33A27B3B49CB0AD7 (c_id), add constraint FK33A27B3B49CB0AD7 foreign key (c_id) references t_persistents (c_id);
alter table t_esl_custom_fields add index FK33A27B3BD0BFC2CD (c_dataset_id), add constraint FK33A27B3BD0BFC2CD foreign key (c_dataset_id) references t_datasets (c_id);
