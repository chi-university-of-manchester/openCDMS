-- Patch to add the c_dis_std_codes column to the t_basic_entrys table
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 7165
alter table t_basic_entrys add c_dis_std_code smallint;
update t_basic_entrys set c_dis_std_code=0;
