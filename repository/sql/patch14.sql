-- Patch to add c_import_enabled and c_import_mapping columns
-- to the t_documents table
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 4410
alter table t_documents add c_import_enabled smallint;
alter table t_documents add c_import_mapping clob(64000);
update t_documents set c_import_enabled=0;
