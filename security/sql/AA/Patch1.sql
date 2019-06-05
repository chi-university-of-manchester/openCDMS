-- Patch to add the c_alias_name and c_alias_id columns to the t_projects table.
-- Patch to add the c_site_name and c_site_id columns to the t_groups table.
-- This patch should be applied to all aa_db deployed from revisions
-- prior to Rev. 
alter table t_projects add c_alias_name varchar(255);
alter table t_projects add c_alias_id varchar(255);
alter table t_groups add c_site_name varchar(255);
alter table t_groups add c_site_id varchar(255);