-- Patch to add the t_bcc_addresses and t_randomisation_email tables for 
-- persisting emails that are queued to be sent to notify of randomisations.
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. xx
create table t_bcc_addresses (c_randomisation_email_id bigint not null, c_bcc_address varchar(255), c_index integer not null, primary key (c_randomisation_email_id, c_index));
create table t_randomisation_email (c_id bigint not null, c_body varchar(255), c_subject varchar(255), c_to_address varchar(255), c_from_address varchar(255), primary key (c_id));
alter table t_bcc_addresses add constraint FK4067FF5A1DC4F763 foreign key (c_randomisation_email_id) references t_randomisation_email;
alter table t_randomisation_email add constraint FK45080E3AB089F7BB foreign key (c_id) references t_persistents;