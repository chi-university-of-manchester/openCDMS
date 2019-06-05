-- Patch to add the c_inactive column to the t_statuses table.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 3775
alter table t_statuses add c_inactive smallint;
update t_statuses set c_inactive=0;