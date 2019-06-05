-- Patch to add editAction to documents and document instances
-- and to add accessAction and editAction to entries and responses.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. xx

alter table t_doc_insts add c_can_edit_action varchar(255);
alter table t_documents add c_can_edit_action varchar(255);
alter table t_documents add c_inst_action varchar(255);
alter table t_documents add c_can_edit_inst_action varchar(255);

alter table t_entrys add c_can_access_action varchar(255);
alter table t_entrys add c_can_edit_action varchar(255);
alter table t_entrys add c_can_access_response_action varchar(255);
alter table t_entrys add c_can_edit_response_action varchar(255);

alter table t_responses add c_can_access_action varchar(255);
alter table t_responses add c_can_edit_action varchar(255);
