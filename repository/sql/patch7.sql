-- Patch to add the c_num_app_docs, c_num_incomp_docs, c_num_pend_docs
-- and c_num_rej_docs columns to the t_records table.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 3369
alter table t_records add c_num_app_docs integer;
alter table t_records add c_num_incomp_docs integer;
alter table t_records add c_num_pend_docs integer;
alter table t_records add c_num_rej_docs integer;
update t_records set c_num_app_docs=0, c_num_incomp_docs=0, c_num_pend_docs=0, c_num_rej_docs=0;
