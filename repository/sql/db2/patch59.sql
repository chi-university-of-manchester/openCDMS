-- Patch to upgrade for Patching Manager update ---
-- This has not been tested on DB2 ---

create table t_audit_log (c_id bigint not null, primary key (c_id));
create table t_auditablechange (c_id bigint not null, c_action varchar(255), c_comment clob(255), c_timestamp timestamp, c_user varchar(255), c_audit_log_id bigint, c_index integer, primary key (c_id));
alter table t_audit_log add constraint FK2A6502D549CB0AD7 foreign key (c_id) references t_persistents;
alter table t_auditablechange add constraint FK5F871DA49CB0AD7 foreign key (c_id) references t_persistents;
alter table t_auditablechange add constraint FK5F871DAC563C4A8 foreign key (c_audit_log_id) references t_audit_log;
alter table t_elements add c_element_patching_action varchar(255);
alter table t_elements add c_autoversion_no integer;
alter table t_entrys add c_locked smallint;
alter table t_elements add c_audit_log_id bigint;
alter table t_elements add constraint FKB62D3462C563C4A8 foreign key (c_audit_log_id) references t_audit_log;


update t_entrys set c_locked=0;
update t_elements set c_autoversion_no = 0;