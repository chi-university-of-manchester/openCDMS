
alter table t_groups modify c_name varchar(255) not null;
alter table t_groups modify c_long_name varchar(255) not null;
alter table t_groups add unique (c_long_name, c_dataset_id);
alter table t_groups add unique (c_name, c_dataset_id);
