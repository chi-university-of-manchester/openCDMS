-- Patch to add the document and record status charts
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 5210
create table t_document_status_charts (c_id bigint not null, primary key (c_id));
create table t_documentstatuschrt_groups (c_chart_id bigint not null, c_group_id bigint not null, c_index integer not null, primary key (c_chart_id, c_index));
create table t_record_status_charts (c_id bigint not null, primary key (c_id));
create table t_recordstatuschrt_groups (c_chart_id bigint not null, c_group_id bigint not null, c_index integer not null, primary key (c_chart_id, c_index));

alter table t_document_status_charts add constraint FKC3A8914993AC95E3 foreign key (c_id) references t_mgmt_charts;
alter table t_documentstatuschrt_groups add constraint FK7DB36CB4373B9A4D foreign key (c_group_id) references t_groups;
alter table t_documentstatuschrt_groups add constraint FK7DB36CB440DB8DFA foreign key (c_chart_id) references t_document_status_charts;
alter table t_record_status_charts add constraint FKC2AE64BF93AC95E3 foreign key (c_id) references t_mgmt_charts;
alter table t_recordstatuschrt_groups add constraint FKC4AE5D7E373B9A4D foreign key (c_group_id) references t_groups;
alter table t_recordstatuschrt_groups add constraint FKC4AE5D7E5ED52544 foreign key (c_chart_id) references t_record_status_charts;
