-- Patch to add tables for collection date management charts
create table t_coll_date_entries (c_coll_date_chart_id bigint not null, c_entry_index integer, c_document_name varchar(255) not null, primary key (c_coll_date_chart_id, c_document_name));
create table t_colldatechrt_groups (c_chart_id bigint not null, c_group_id bigint not null, c_index integer not null, primary key (c_chart_id, c_index));
create table t_collection_date_charts (c_id bigint not null, primary key (c_id));

alter table t_coll_date_entries add constraint FKA20E1C678E87539 foreign key (c_coll_date_chart_id) references t_collection_date_charts;
alter table t_colldatechrt_groups add constraint FKAD60AE87373B9A4D foreign key (c_group_id) references t_groups;
alter table t_colldatechrt_groups add constraint FKAD60AE87CBEC3EDB foreign key (c_chart_id) references t_collection_date_charts;
alter table t_collection_date_charts add constraint FKFC517E1093AC95E3 foreign key (c_id) references t_mgmt_charts;
