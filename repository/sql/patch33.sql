-- Patch to add the c_esl_trigger column to the t_cons_form_groups table
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 7179
alter table t_cons_form_groups add c_esl_trigger smallint;
update t_cons_form_groups set c_esl_trigger=0;
