-- Patch to add the tables for the BasicStatisticsChart 
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 7050
create table t_basic_stats_charts (c_id bigint not null, primary key (c_id));
alter table t_basic_stats_charts add constraint FKC861761193AC95E3 foreign key (c_id) references t_mgmt_charts;
