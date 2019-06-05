-- Patch to add new system time column to t_change_history
-- and t_provenance
alter table t_change_history add c_when_system timestamp;
alter table t_provenance add c_timestamp_system timestamp;