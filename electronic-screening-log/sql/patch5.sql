-- Patch to add new column c_locked to table t_subjects
-- This patch should be applied to all databases deployed from 
-- revisions prior to Rev. 7179
alter table t_subjects add c_locked smallint;
update t_subjects set c_locked=0;