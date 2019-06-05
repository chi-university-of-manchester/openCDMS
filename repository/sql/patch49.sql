-- Patch addresses changes in the repository schema as a result of
-- element library requirements from the NIHR.
-- Element MetatData table is removed, as it is only in the element library
-- schema.
-- Validation rules table has columns added.

update t_elements set t_element_metadata_id = null;
alter table t_elements drop foreign key FKB62D3462905C37CE; 
delete from t_element_metadata; 
alter table t_element_metadata drop foreign key FK31C0817D49CB0AD7; 
drop table t_element_metadata; 
alter table t_val_rules add c_name varchar(255); 
alter table t_val_rules add c_lsid_id bigint; 
alter table t_val_rules add c_lsid_instance_id bigint; 
alter table t_val_rules add constraint FKCC79D04E66781B07 foreign key (c_lsid_id) references t_lsid;
alter table t_val_rules add constraint FKCC79D04EF738ACF foreign key (c_lsid_instance_id) references t_lsid;
