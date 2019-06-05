alter table t_bcc_addresses drop foreign key FK4067FF5AF5F8BC2D;
alter table t_queued_emails drop foreign key FK1F781C18B089F7BB;
alter table t_emails drop foreign key FK65FF7A82B089F7BB;

drop table t_bcc_addresses;
drop table t_emails;
drop table t_queued_emails;

create table t_bcc_addresses (c_queued_email_id bigint not null, c_bcc_address varchar(255), c_index integer not null, primary key (c_queued_email_id, c_index)) type=InnoDB;
create table t_emails (c_id bigint not null auto_increment, c_subject varchar(255), c_body text, primary key (c_id)) type=InnoDB;
create table t_queued_emails (c_id bigint not null auto_increment, c_body text, c_subject varchar(255), c_to_address varchar(255), c_from_address varchar(255), primary key (c_id)) type=InnoDB;
alter table t_bcc_addresses add index FK4067FF5A8EEE139E (c_queued_email_id), add constraint FK4067FF5A8EEE139E foreign key (c_queued_email_id) references t_queued_emails (c_id);
