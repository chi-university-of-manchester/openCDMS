-- Patch to add new columns to t_changes, to cope with fields
-- having contents greater than 255 chars.
-- This patch should be applied to all databases deployed from 
-- revisions prior to Rev. 5729

alter table t_change add column c_prev_value2 clob(4096);
alter table t_change add column c_new_value2 clob(4096);