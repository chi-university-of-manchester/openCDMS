-- Patch to add c_long_name column to t_groups and the c_view_action
-- column to t_mgmt_reports
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 7772

alter table t_groups add c_long_name varchar(500);
alter table t_mgmt_reports add c_view_action varchar(255);
