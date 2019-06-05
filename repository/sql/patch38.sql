-- Patch to add c_long_run column to t_documents
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 7517
alter table t_documents add column c_long_run smallint;
update t_documents set c_long_run=0;
