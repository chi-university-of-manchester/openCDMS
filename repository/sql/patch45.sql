-- Patch to add c_is_randomised column to t_doc_insts
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 8081

alter table t_doc_insts add c_is_randomised smallint;
