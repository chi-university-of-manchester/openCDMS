insert into t_persistents(c_version) values (0);
insert into t_std_responses(c_id, c_description, c_code, c_for_derived) (select max(c_id), 'Data not known', 960, 0 from t_persistents);
insert into t_persistents(c_version) values (0);
insert into t_std_responses(c_id, c_description, c_code, c_for_derived) (select max(c_id), 'Not applicable', 970, 0 from t_persistents);
insert into t_persistents(c_version) values (0);
insert into t_std_responses(c_id, c_description, c_code, c_for_derived) (select max(c_id), 'Refused to answer', 980, 0 from t_persistents);
insert into t_persistents(c_version) values (0);
insert into t_std_responses(c_id, c_description, c_code, c_for_derived) (select max(c_id), 'Data unable to be captured', 999, 1 from t_persistents);
