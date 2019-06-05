-- Patch to new columns to lsid table
-- and to add t_lsid_authority and t_lsid_namespace tables
-- This patch should be applied to all databases deployed from revisions
-- prior to Rev. 7883
-- Note that the following columns in t_lsid are no longer used:
	-- c_authority_id
	-- c_namespace_id
	-- c_full_lsid

alter table t_lsid add c_auth_id bigint default 0 not null;
alter table t_lsid add c_ns_id   bigint default 0 not null;
alter table t_lsid add c_next_revision varchar(255);
alter table t_lsid add c_previous_revision varchar(255);

create table t_lsid_authority (c_auth_id bigint not null, c_authority_id varchar(255), primary key (c_auth_id));
create table t_lsid_namespace (c_ns_id bigint not null, c_namespace varchar(255), primary key (c_ns_id));

alter table t_lsid add constraint FKCB5FB5CDB63DA56E foreign key (c_auth_id) references t_lsid_authority;
alter table t_lsid add constraint FKCB5FB5CD815BB29 foreign key (c_ns_id) references t_lsid_namespace;
alter table t_lsid_authority add constraint FK4D599B51619FF96 foreign key (c_auth_id) references t_persistents;
alter table t_lsid_namespace add constraint FK4008E9696705B759 foreign key (c_ns_id) references t_persistents;
