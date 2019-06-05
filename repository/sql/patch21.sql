-- Patch to add the c_rev_rem_count column to the t_datasets table.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 5234
alter table t_datasets add column c_rev_rem_count integer;
update t_datasets set c_rev_rem_count=0;