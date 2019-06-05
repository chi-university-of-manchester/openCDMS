-- Patch to add external derived entries tables
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 5067
create table t_ext_variables (c_entry_id bigint not null, c_input_id bigint not null, c_variable_name varchar(255) not null, primary key (c_entry_id, c_variable_name));
create table t_external_derived_entries (c_id bigint not null, c_external_transformer_id bigint, primary key (c_id));
alter table t_ext_variables add constraint FK57A8844E1837AAD foreign key (c_entry_id) references t_external_derived_entries;
alter table t_ext_variables add constraint FK57A8844E807C38B9 foreign key (c_input_id) references t_basic_entrys;
alter table t_external_derived_entries add constraint FK71CB5565E6D02D24 foreign key (c_id) references t_basic_entrys;
alter table t_external_derived_entries add constraint FK71CB55658B0B4589 foreign key (c_external_transformer_id) references t_transformers;