-- Patch to add the c_for_derived column to the t_std_responses table.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 2462
alter table t_std_responses add c_for_derived smallint;
update t_std_responses set c_for_derived=0 where c_code=960;
update t_std_responses set c_for_derived=0 where c_code=970;
update t_std_responses set c_for_derived=0 where c_code=980;
update t_std_responses set c_for_derived=1 where c_code=999;
