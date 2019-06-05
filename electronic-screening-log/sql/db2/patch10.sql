alter table t_projects add c_use_meds_service smallint;
update t_projects set c_use_meds_service = 0;