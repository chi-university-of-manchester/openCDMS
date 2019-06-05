alter table t_export_requests add c_is_participant_register bit;
update t_export_requests set c_is_participant_register = 0;