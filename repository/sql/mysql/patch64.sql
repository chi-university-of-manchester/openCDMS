create table t_import_requests (c_id bigint not null auto_increment, c_project_code varchar(255), c_user varchar(255), c_request_date datetime, c_remote_file_path varchar(255), c_file_path varchar(255), c_data_type varchar(255), c_immediate bit, c_status varchar(255), c_current_line integer, c_completed datetime, primary key (c_id)) type=InnoDB;