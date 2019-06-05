-- Patch to add the c_show_total column to the t_mgmt_chart table.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 4696
alter table t_prj_summ_charts add column c_show_total smallint;
update t_prj_summ_charts set c_show_total=0;
