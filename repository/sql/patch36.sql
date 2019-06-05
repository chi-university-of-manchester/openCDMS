-- Patch to add lsid table,element meta_data table, and element_relationship table (and references to them)
-- by Elements and Components.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 7475

create table t_lsid (c_lsid_id bigint not null, c_authority_id varchar(255), c_namespace_id varchar(255), c_revision_id varchar(255), c_object_id varchar(255), c_full_lsid varchar(255), primary key (c_lsid_id));

create table t_element_metadata (c_id bigint not null, c_activity_description varchar(255), c_date timestamp, c_lsid varchar(255), c_element_status varchar(255), c_registrar varchar(255), c_replaced_by varchar(255), c_terminological_ref varchar(255), c_who varchar(255), primary key (c_id));

create table t_element_relationship (c_id bigint not null, c_lsid varchar(255), c_repopulate_method varchar(255), c_element_class varchar(255), c_indexed_relationship smallint, c_relationship_type varchar(255), primary key (c_id));

alter table t_elements add column c_element_metadata_id bigint;
alter table t_components add column c_lsid_id bigint;
alter table t_components add column c_lsid_instance_id bigint;

alter table t_components add constraint FKA2B1C7C166781B07 foreign key (c_lsid_id) references t_lsid;
alter table t_components add constraint FKA2B1C7C1F738ACF foreign key (c_lsid_instance_id) references t_lsid;
alter table t_element_metadata add constraint FK31C0817D49CB0AD7 foreign key (c_id) references t_persistents;
alter table t_element_relationship add constraint FK144FA58649CB0AD7 foreign key (c_id) references t_persistents;
alter table t_elements add constraint FKB62D3462905C37CE foreign key (c_element_metadata_id) references t_element_metadata;
alter table t_lsid add constraint FKCB5FB5CD47F8061C foreign key (c_lsid_id) references t_persistents;
