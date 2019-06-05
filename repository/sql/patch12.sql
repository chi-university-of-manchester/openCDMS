-- Patch to add the t_export_requests and t_ex_req_groups tables.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 4291
create table t_ex_req_groups (c_req_id bigint not null, c_group varchar(255) not null, c_index integer not null, primary key (c_req_id, c_index));
create table t_export_requests (c_id bigint generated by default as identity, c_version integer not null, c_completed timestamp, c_path varchar(255), c_project_code varchar(255), c_request_date timestamp, c_requestor varchar(255), c_status varchar(255), primary key (c_id));
alter table t_ex_req_groups add constraint FKF8563316204C8BE5 foreign key (c_req_id) references t_export_requests;
