-- Patch to increase the size of text that can be stored in
-- t_long_text_values
create table t_long_text_values_copy (c_id bigint not null, c_value2 clob(4096), primary key (c_id));
insert into t_long_text_values_copy (c_id, c_value2) (select c_id, c_value2 from t_long_text_values);

alter table t_long_text_values drop constraint FKDACDBB9C9C3B407F;
drop table t_long_text_values;

create table t_long_text_values (c_id bigint not null, c_value clob(32768), primary key (c_id));
insert into t_long_text_values (c_id, c_value) (select c_id, c_value2 from t_long_text_values_copy);
alter table t_long_text_values add constraint FKDACDBB9C9C3B407F foreign key (c_id) references t_values;

drop table t_long_text_values_copy;

