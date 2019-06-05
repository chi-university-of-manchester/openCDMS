-- Patch to add c_ext_id_used column to t_datasets and 
-- c_ext_id to t_records
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 7701

alter table t_datasets add c_ext_id_used smallint;
update t_datasets set c_ext_id_used=0;

alter table t_records add c_ext_id varchar(255);
