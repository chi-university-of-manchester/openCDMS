-- Add c_def_vals_for_disabled_vars column to t_derived_entrys
alter table t_derived_entrys add c_def_vals_for_disabled_vars smallint;
update t_derived_entrys set c_def_vals_for_disabled_vars=0;

