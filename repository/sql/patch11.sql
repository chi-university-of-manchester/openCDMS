-- Patch to add the c_consent_modified and c_status_modified columns to 
-- the t_records table and set the values of these columns to the current
-- date/time for all existing records.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 4070
alter table t_records add c_consent_modified timestamp;
alter table t_records add c_status_modified timestamp;
update t_records set c_consent_modified = CURRENT TIMESTAMP, c_status_modified = CURRENT TIMESTAMP;
