alter table t_datasets add c_use_meds_service bit;
update t_datasets set c_use_meds_service = 0;