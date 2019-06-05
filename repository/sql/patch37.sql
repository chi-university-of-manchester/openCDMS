-- Patch to add c_dis_part_dates column 
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 7443

alter table t_date_entrys add column c_dis_part_dates smallint;
update t_date_entrys set c_dis_part_dates=0;
