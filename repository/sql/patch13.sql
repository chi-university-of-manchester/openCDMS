-- Patch to add t_sites, with links from t_groups
-- and added site to t_records
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 4351

create table t_sites (c_id bigint not null, c_site_name varchar(255), c_site_id varchar(255), c_geographic_code varchar(255), c_group_id bigint not null, c_index integer, primary key (c_id));
alter table t_records add c_site_id bigint;
alter table t_records add constraint FKFC191177C8524107 foreign key (c_site_id) references t_sites;
alter table t_sites add constraint FKA0F5468149CB0AD7 foreign key (c_id) references t_persistents;
alter table t_sites add constraint FKA0F54681373B9A4D foreign key (c_group_id) references t_groups;
