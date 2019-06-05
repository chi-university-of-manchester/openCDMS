-- Patch required for all DARE functionality
-- Required for all deployments prior to r****

alter table t_datasets add c_ext_id_as_primary bit;
alter table t_records add c_ext_id_as_primary bit;
update t_datasets set c_ext_id_as_primary =0;
update t_records set c_ext_id_as_primary = 0;