-- Patch required for Sample Tracking
-- Required for all deployments prior to r****

create table t_sampletracking_action (c_id bigint not null auto_increment, c_project_code varchar(255), c_status varchar(255), c_action varchar(255), c_targets varchar(255), c_subject varchar(255), c_message varchar(255), primary key (c_id)) type=InnoDB;
create table t_sampletracking_config (c_id bigint not null auto_increment, c_project_code varchar(255), c_tracking bit, c_auto_participant_id bit, c_using_externalid bit, c_sample_counter bigint, c_separator varchar(255), c_label_width integer, c_label_height integer, c_font_size integer, c_print_barcodes bit, c_participant_regex varchar(255), c_participant_regex_desc varchar(255), c_auto_sample_id bit, c_sample_regex varchar(255), c_sample_regex_description varchar(255), primary key (c_id)) type=InnoDB;
create table t_sampletracking_participant (c_id bigint not null auto_increment, c_record_id varchar(255) not null unique, c_project_code varchar(255), c_identifier varchar(255), primary key (c_id)) type=InnoDB;
create table t_sampletracking_sample (c_id bigint not null auto_increment, c_participant_id bigint not null, c_index integer, primary key (c_id)) type=InnoDB;
create table t_sampletracking_sample_revision (c_id bigint not null auto_increment, c_sample_id bigint not null, c_user varchar(255), c_participant_identifier varchar(255), c_identifier varchar(255), c_timestamp datetime, c_status varchar(255), c_sampletype varchar(255), c_tubetype varchar(255), c_tracking_id varchar(255), c_index integer, primary key (c_id)) type=InnoDB;
create table t_sampletracking_sampletypes (c_config_id bigint not null, c_sampletype varchar(255), c_index integer not null, primary key (c_config_id, c_index)) type=InnoDB;
create table t_sampletracking_statuses (c_config_id bigint not null, c_status varchar(255), c_index integer not null, primary key (c_config_id, c_index)) type=InnoDB;
create table t_sampletracking_tubetypes (c_config_id bigint not null, c_tubetype varchar(255), c_index integer not null, primary key (c_config_id, c_index)) type=InnoDB;
alter table t_sampletracking_sample add index FK8E2D9BBDA5BE41EB (c_participant_id), add constraint FK8E2D9BBDA5BE41EB foreign key (c_participant_id) references t_sampletracking_participant (c_id);
alter table t_sampletracking_sample_revision add index FKDC03321D50B471C9 (c_sample_id), add constraint FKDC03321D50B471C9 foreign key (c_sample_id) references t_sampletracking_sample (c_id);
alter table t_sampletracking_sampletypes add index FKE3A8ADCC3E26AC9 (c_config_id), add constraint FKE3A8ADCC3E26AC9 foreign key (c_config_id) references t_sampletracking_config (c_id);
alter table t_sampletracking_statuses add index FK920E9573C3E26AC9 (c_config_id), add constraint FK920E9573C3E26AC9 foreign key (c_config_id) references t_sampletracking_config (c_id);
alter table t_sampletracking_tubetypes add index FKC72C0982C3E26AC9 (c_config_id), add constraint FKC72C0982C3E26AC9 foreign key (c_config_id) references t_sampletracking_config (c_id);
