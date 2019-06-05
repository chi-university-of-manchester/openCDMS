-- Patch to add the t_abstract_chart_items table
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 7230
create table t_abstract_chart_items (c_id bigint not null, c_simple_chart_row_id bigint not null, c_index integer, primary key (c_id));
create table t_esl_chart_items (c_id bigint not null, c_fieldname varchar(255), primary key (c_id));

create table t_simple_chart_items_temp (c_id bigint not null, c_doc_occ_id bigint not null, c_entry_id bigint not null, c_sec_occ_id bigint not null, c_options varchar(255), c_label_options varchar(255), c_simple_chart_row_id bigint not null, c_index integer, primary key (c_id));

insert into t_simple_chart_items_temp (c_id, c_doc_occ_id, c_entry_id, c_sec_occ_id, c_options, c_label_options, c_simple_chart_row_id, c_index) (select c_id, c_doc_occ_id, c_entry_id, c_sec_occ_id, c_options, c_label_options, c_simple_chart_row_id, c_index from t_simple_chart_items;

drop table t_simple_chart_items;

create table t_simple_chart_items (c_id bigint not null, c_doc_occ_id bigint not null, c_entry_id bigint not null, c_sec_occ_id bigint not null, c_options varchar(255), c_label_options varchar(255), primary key (c_id));

insert into t_abstract_chart_items (c_id, c_simple_chart_row_id, c_index) (select c_id, c_simple_chart_row_id, c_index from t_simple_chart_items_temp);

insert into t_simple_chart_items (c_id, c_doc_occ_id, c_entry_id, c_sec_occ_id, c_options, c_label_options) (select c_id, c_doc_occ_id, c_entry_id, c_sec_occ_id, c_options, c_label_options from t_simple_chart_items_temp);

alter table t_abstract_chart_items add constraint FKA4791A6D49CB0AD7 foreign key (c_id) references t_persistents;
alter table t_abstract_chart_items add constraint FKA4791A6D77D8996F foreign key (c_simple_chart_row_id) references t_simple_chart_rows;
alter table t_esl_chart_items add constraint FK8967CD136A3D1257 foreign key (c_id) references t_abstract_chart_items;
alter table t_simple_chart_items add constraint FK26F542BD6A3D1257 foreign key (c_id) references t_abstract_chart_items;
alter table t_simple_chart_items add constraint FK26F542BD9C24716D foreign key (c_entry_id) references t_entrys;
alter table t_simple_chart_items add constraint FK26F542BD43F87002 foreign key (c_sec_occ_id) references t_sec_occs;
alter table t_simple_chart_items add constraint FK26F542BDD307CAA3 foreign key (c_doc_occ_id) references t_doc_occs;

drop table t_simple_chart_items_temp;
