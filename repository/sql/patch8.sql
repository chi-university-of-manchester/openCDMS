-- Patch to add the c_frequency column to the t_mgmt_reports table.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 3758
alter table t_mgmt_reports add c_frequency varchar(255);
