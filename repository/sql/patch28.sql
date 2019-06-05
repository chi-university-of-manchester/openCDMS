-- Patch to add a list of consultants to sites
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 6941
create table t_site_consultants (c_site_id bigint not null, c_consultant varchar(255), c_index integer not null, primary key (c_site_id, c_index));
alter table t_site_consultants add constraint FK49B34CCBC8524107 foreign key (c_site_id) references t_sites;
alter table t_records add column c_consultant varchar(255);