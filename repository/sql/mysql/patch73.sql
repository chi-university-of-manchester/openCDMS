alter table t_datasets add c_force_record_creation bit;
update t_datasets set c_force_record_creation = 0;