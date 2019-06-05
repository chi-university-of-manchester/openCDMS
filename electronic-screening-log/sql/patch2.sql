-- Patch to alter the t_bcc_addresses, t_emails, t_emails_maps and
-- t_queued_emails tables for persisting emails that are queued to 
-- be sent to notify of randomisations or alterations to subjects.
-- This patch should be applied to all databases deployed from 
-- revisions prior to Rev. 5347
drop table t_bcc_addresses;
create table t_bcc_addresses (c_queued_email_id bigint not null, c_bcc_address varchar(255), c_index integer not null, primary key (c_queued_email_id, c_index));

drop table t_emails;
drop table t_emails_map;
create table t_emails (c_id bigint not null, c_subject varchar(255), c_body clob(4096), primary key (c_id));
create table t_emails_map (c_randomisation_id bigint not null, c_subject varchar(255), c_body clob(4096), c_name varchar(255) not null, primary key (c_randomisation_id, c_name));

drop table t_randomisation_email;
create table t_queued_emails (c_id bigint not null, c_body clob(4096), c_subject varchar(255), c_to_address varchar(255), c_from_address varchar(255), primary key (c_id));

alter table t_bcc_addresses add constraint FK4067FF5AF5F8BC2D foreign key (c_queued_email_id) references t_queued_emails;
alter table t_queued_emails add constraint FK1F781C18B089F7BB foreign key (c_id) references t_persistents;
alter table t_emails add constraint FK65FF7A82B089F7BB foreign key (c_id) references t_persistents;
alter table t_emails_map add constraint FKD073E1F21C20B49 foreign key (c_randomisation_id) references t_randomisation;