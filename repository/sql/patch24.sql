-- Patch to add the c_deleted column to the t_records table.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 5736
alter table t_records add c_deleted smallint;
update t_records set c_deleted=0;