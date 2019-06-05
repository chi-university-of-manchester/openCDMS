-- Patch to add the c_esl_used and c_rnd_req columns to the t_datasets table
-- and the c_rnd_trigger column to the t_doc_occs table.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 3345
alter table t_datasets add c_esl_used smallint;
alter table t_datasets add c_rnd_req smallint;
update t_datasets set c_esl_used=0, c_rnd_req=0;
alter table t_doc_occs add c_rnd_trigger smallint;
update t_doc_occs set c_rnd_trigger=0;
