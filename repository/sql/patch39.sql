-- Patch to add c_format column to t_export_requests
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 7573
alter table t_export_requests add column c_format varchar(255);
update t_export_requests set c_format='multi';
