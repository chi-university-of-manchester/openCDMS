-- Add c_deleted columns to t_responses and t_sec_occ_insts
alter table t_responses add c_deleted smallint;
update t_responses set c_deleted=0;
alter table t_sec_occ_insts add c_deleted smallint;
update t_sec_occ_insts set c_deleted=0;
