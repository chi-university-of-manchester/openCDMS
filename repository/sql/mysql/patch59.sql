-- Patch to upgrade for Patching Manager update ---
-- This has not been tested on DB2 ---

create table t_audit_log (c_id bigint not null, primary key (c_id)) type=InnoDB;
create table t_auditablechange (c_id bigint not null, c_action varchar(255), c_comment text, c_timestamp datetime, c_user varchar(255), c_audit_log_id bigint, c_index integer, primary key (c_id)) type=InnoDB;
alter table t_audit_log add index FK2A6502D549CB0AD7 (c_id), add constraint FK2A6502D549CB0AD7 foreign key (c_id) references t_persistents (c_id);
alter table t_auditablechange add index FK5F871DA49CB0AD7 (c_id), add constraint FK5F871DA49CB0AD7 foreign key (c_id) references t_persistents (c_id);
alter table t_auditablechange add index FK5F871DAC563C4A8 (c_audit_log_id), add constraint FK5F871DAC563C4A8 foreign key (c_audit_log_id) references t_audit_log (c_id);
alter table t_elements add c_element_patching_action varchar(255);
alter table t_elements add c_autoversion_no integer;
alter table t_entrys add c_locked bit;
alter table t_elements add c_audit_log_id bigint unique;
alter table t_elements add index FKB62D3462C563C4A8 (c_audit_log_id), add constraint FKB62D3462C563C4A8 foreign key (c_audit_log_id) references t_audit_log (c_id);

update t_entrys set c_locked=0;
update t_elements set c_autoversion_no = 0;