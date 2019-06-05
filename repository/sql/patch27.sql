-- Patch to add the name of an RBACAction to the management reports table
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 6731
alter table t_mgmt_reports add column c_action varchar(255);