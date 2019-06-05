-- Patch to add t_ex_req_docs table
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 4698
create table t_ex_req_docs (c_req_id bigint not null, c_dococc bigint not null, c_index integer not null, primary key (c_req_id, c_index));
alter table t_ex_req_docs add constraint FK1BB0E3FD204C8BE5 foreign key (c_req_id) references t_export_requests;
