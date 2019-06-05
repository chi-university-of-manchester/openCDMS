-- Patch to add c_rbac_action column to t_documents and t_doc_occs
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. ...
alter table t_documents add column c_rbac_action varchar(40);
update t_documents set c_rbac_action='ACTION_DR_DOC_STANDARD';
alter table t_doc_insts add column c_rbac_action varchar(40);
update t_doc_insts set c_rbac_action='ACTION_DR_DOC_STANDARD';
