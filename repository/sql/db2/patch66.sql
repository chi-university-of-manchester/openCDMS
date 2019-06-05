alter table t_datasets add c_force_record_creation smallint;
update t_datasets set c_force_record_creation = 0;