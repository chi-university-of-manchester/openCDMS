alter table t_datasets add c_show_rand_treatment bit;
update t_datasets set c_show_rand_treatment = 0;