-- Patch to add generic states to the t_statuses table and to add state
-- transitions to t_doc_groups table.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 6098
alter table t_statuses add column c_generic_state varchar(255);
create table t_allowed_record_statuses (c_document_group_id bigint not null, c_status_id bigint not null, c_index integer not null,  primary key (c_document_group_id, c_index));
create table t_prerequisite_groups (c_document_group_id bigint not null, c_prerequisite_id bigint not null, c_index integer not null, primary key (c_document_group_id, c_index));
alter table t_doc_groups add column c_update_status_id bigint;
alter table t_prerequisite_groups add column c_prerequisite_id bigint;
alter table t_allowed_record_statuses add constraint FK280CA8EC3CC8005E foreign key (c_document_group_id) references t_doc_groups;
alter table t_allowed_record_statuses add constraint FK280CA8ECA1C285E7 foreign key (c_status_id) references t_statuses;
alter table t_doc_groups add constraint FKF77BC7663D6FCB29 foreign key (c_update_status_id) references t_statuses;
alter table t_prerequisite_groups add constraint FK5B70E732D1C51F23 foreign key (c_prerequisite_id) references t_doc_groups;
alter table t_prerequisite_groups add constraint FK5B70E7323CC8005E foreign key (c_document_group_id) references t_doc_groups;
