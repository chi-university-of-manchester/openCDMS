-- Patch to add the c_show_header column to the t_reports table.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 5210
alter table t_reports add column c_show_header smallint default 1;
