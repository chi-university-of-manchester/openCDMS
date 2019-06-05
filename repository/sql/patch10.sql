-- Patch to add the c_send_monthly column to the t_datasets table.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 3881
alter table t_datasets add c_send_monthly smallint;
update t_datasets set c_send_monthly=0;
