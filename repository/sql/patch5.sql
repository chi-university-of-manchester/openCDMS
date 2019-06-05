-- Patch to add the c_user and c_created columns to the t_identifiers table.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 3224
alter table t_identifiers add c_created timestamp;
alter table t_identifiers add c_user varchar(255);
