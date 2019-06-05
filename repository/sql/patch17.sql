-- Patch to add t_treatment_charts and t_treatmentchrt_groups tables
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 4981
create table t_treatment_charts (c_id bigint not null, c_end_date timestamp, c_start_date timestamp, primary key (c_id));
create table t_treatmentchrt_groups (c_chart_id bigint not null, c_group_id bigint not null, c_index integer not null, primary key (c_chart_id, c_index));
alter table t_treatment_charts add constraint FK11A78A6793AC95E3 foreign key (c_id) references t_mgmt_charts;
alter table t_treatmentchrt_groups add constraint FK1AC3671F373B9A4D foreign key (c_group_id) references t_groups;
alter table t_treatmentchrt_groups add constraint FK1AC3671F440217CF foreign key (c_chart_id) references t_treatment_charts;