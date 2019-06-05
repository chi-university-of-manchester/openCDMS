-- Patch to add c_immediate column to t_export_requests
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 8823

alter table t_export_requests add c_immediate smallint;
update t_export_requests set c_immediate=0;
