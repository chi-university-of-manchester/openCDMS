-- Patch to add new columns to t_export_request to store
-- the MD5 and SHA 1 hashes of the zipped export file, and to store 
-- whether codes and/or values are to be shown.
-- Add new table to store doc statuses to be exported in the export request
-- Add new tables to store the document occurrence ids and entry ids
alter table t_export_requests add c_sha1_path varchar(255);
alter table t_export_requests add c_md5_path varchar(255);
alter table t_export_requests add c_is_show_codes bit; 
alter table t_export_requests add c_is_show_values bit;
create table t_ex_req_doc_statuses (c_req_id bigint not null, c_docstatus varchar(255) not null, c_index integer not null, primary key (c_req_id, c_index)) type=InnoDB;
create table t_export_documents (c_id bigint not null auto_increment, c_version integer not null, c_dococc_id bigint, c_ex_req_id bigint, c_index integer, primary key (c_id)) type=InnoDB;
create table t_ex_docs_entries (c_ex_entries_id bigint not null, c_entry bigint not null, c_index integer not null, primary key (c_ex_entries_id, c_index)) type=InnoDB;
alter table t_ex_req_doc_statuses add index FK5B009309204C8BE5 (c_req_id), add constraint FK5B009309204C8BE5 foreign key (c_req_id) references t_export_requests (c_id);
alter table t_export_documents add index FK1E9D285866DD1399 (c_exportreq_id), add constraint FK1E9D285866DD1399 foreign key (c_exportreq_id) references t_export_requests (c_id);
alter table t_ex_docs_entries add index FKE9A5A8CD6A60697 (c_ex_entries_id), add constraint FKE9A5A8CD6A60697 foreign key (c_ex_entries_id) references t_export_documents (c_id);

UPDATE t_export_requests SET c_is_show_values=1 WHERE c_is_show_codes is NULL;
UPDATE t_export_requests SET c_is_show_values=1 WHERE c_is_show_values is NULL;
