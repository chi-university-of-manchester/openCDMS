-- Patch to add the tables and columns for dual data entry functionality
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. XXXX

alter table t_datasets add c_prim_proj_code varchar(255);
alter table t_datasets add c_sec_proj_code varchar(255);

alter table t_doc_occs add c_prim_occ_index bigint;
alter table t_doc_occs add c_sec_occ_index bigint;

alter table t_documents add c_prim_doc_index bigint;
alter table t_documents add c_sec_doc_index bigint;

create table t_grp_sec_grps (c_grp_id bigint not null, c_sec_grp varchar(255) not null, c_index integer not null, primary key (c_grp_id, c_index));

alter table t_records add c_prim_ident varchar(255);
alter table t_records add c_sec_ident varchar(255);

alter table t_grp_sec_grps add constraint FK5E4E2B611F038527 foreign key (c_grp_id) references t_groups;


