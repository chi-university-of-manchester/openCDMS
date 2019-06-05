-- Patch to add the tables for the stdCodeStatusChart 
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 6996
create table t_stdcode_status_charts (c_id bigint not null, c_per_document smallint, c_per_entry smallint, primary key (c_id));
create table t_stdcodestatuschrt_dococcs (c_chart_id bigint not null, c_dococc_id bigint not null, c_index integer not null, primary key (c_chart_id, c_index));
create table t_stdcodestatuschrt_groups (c_chart_id bigint not null, c_group_id bigint not null, c_index integer not null, primary key (c_chart_id, c_index));
alter table t_stdcode_status_charts add constraint FK599D484893AC95E3 foreign key (c_id) references t_mgmt_charts;
alter table t_stdcodestatuschrt_dococcs add constraint FK22F0071BBAAEAEBC foreign key (c_dococc_id) references t_doc_occs;
alter table t_stdcodestatuschrt_dococcs add constraint FK22F0071BC93A5003 foreign key (c_chart_id) references t_stdcode_status_charts;
alter table t_stdcodestatuschrt_groups add constraint FK66EE155373B9A4D foreign key (c_group_id) references t_groups;
alter table t_stdcodestatuschrt_groups add constraint FK66EE155C93A5003 foreign key (c_chart_id) references t_stdcode_status_charts;
