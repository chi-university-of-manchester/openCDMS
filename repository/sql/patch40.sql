-- Patch to add c_lower_lte and c_upper_gte columns to t_num_val_rules
-- and t_int_val_rules
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 7610
alter table t_num_val_rules add column c_lower_lte smallint;
alter table t_num_val_rules add column c_upper_gte smallint;
alter table t_int_val_rules add column c_lower_lte smallint;
alter table t_int_val_rules add column c_upper_gte smallint;
update t_num_val_rules set c_lower_lte=0, c_upper_gte=0;
update t_int_val_rules set c_lower_lte=0, c_upper_gte=0;
