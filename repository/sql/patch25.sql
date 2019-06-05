-- Patch to add the c_stud_ent_date column to the t_records table.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 6028
alter table t_records add c_stud_ent_date timestamp;
update t_records set c_stud_ent_date=c_sch_st_date;