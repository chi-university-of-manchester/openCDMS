
create table t_export_external_queries (c_id bigint not null auto_increment, c_version integer not null, c_name varchar(255), c_description varchar(255), c_project_code varchar(255), c_document_name varchar(255), c_entry_name varchar(255), c_url varchar(255), c_query longtext, c_user varchar(255), c_password varchar(255), primary key (c_id)) type=InnoDB;

create index group_prefix_index on t_identifiers (c_group_prefix);
create index project_prefix_index on t_identifiers (c_proj_prefix);
create index suffix_index on t_identifiers (c_suffix);
