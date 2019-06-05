-- Patch to add the c_trans_w_std_codes column to the t_external_derived_entries table
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 7338
alter table t_external_derived_entries add c_trans_w_std_codes smallint;
update t_external_derived_entries set c_trans_w_std_codes=0;
