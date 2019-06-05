-- Patch to add new table t_queued_sms to store queued sms messages
-- This patch should be applied to all databases deployed from 
-- revisions prior to Rev. xxx
create table t_queued_sms (c_id bigint not null, c_message clob(160), c_recipient_name varchar(255), c_recipient_number varchar(255), primary key (c_id));
alter table t_queued_sms add constraint FK30264938B089F7BB foreign key (c_id) references t_persistents;