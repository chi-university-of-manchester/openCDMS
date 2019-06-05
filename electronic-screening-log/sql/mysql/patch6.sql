create table t_cust_field_values (c_cust_field_id bigint not null, c_value varchar(255), c_index integer not null, primary key (c_cust_field_id, c_index)) type=InnoDB;
create table t_custom_fields (c_id bigint not null, c_project_id bigint not null, c_index integer, primary key (c_id)) type=InnoDB;
create table t_custom_values (c_id bigint not null, c_name varchar(255), c_value varchar(255), c_subject_id bigint not null, c_index integer, primary key (c_id)) type=InnoDB;
alter table t_cust_field_values add index FKBA1AC368F0E3BB0C (c_cust_field_id), add constraint FKBA1AC368F0E3BB0C foreign key (c_cust_field_id) references t_custom_fields (c_id);
alter table t_custom_fields add index FKB79B56BCB089F7BB (c_id), add constraint FKB79B56BCB089F7BB foreign key (c_id) references t_persistents (c_id);
alter table t_custom_fields add index FKB79B56BC7E3888E9 (c_project_id), add constraint FKB79B56BC7E3888E9 foreign key (c_project_id) references t_projects (c_id);
alter table t_custom_values add index FKD27B7525B089F7BB (c_id), add constraint FKD27B7525B089F7BB foreign key (c_id) references t_persistents (c_id);
alter table t_custom_values add index FKD27B7525748CEC09 (c_subject_id), add constraint FKD27B7525748CEC09 foreign key (c_subject_id) references t_subjects (c_id);
