-- Patch to add new column c_ok_to_delete_esl_data to table t_projects
alter table t_projects add c_ok_to_delete_esl_data smallint;
update t_projects set c_ok_to_delete_esl_data=0;