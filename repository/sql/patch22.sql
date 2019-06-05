-- Patch to add the c_locked column to the t_doc_occs table.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 5488
alter table t_doc_occs add c_locked smallint;
update t_doc_occs set c_locked=0;
