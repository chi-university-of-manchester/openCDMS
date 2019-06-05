create table t_group_attributes (c_id bigint not null, c_attribute_name varchar(255), c_detail1 varchar(255), c_detail2 varchar(255), c_detail3 varchar(255), c_detail4 varchar(255), c_group_id bigint not null, c_ga_index integer, primary key (c_id)) type=InnoDB;
create table t_groupattribute_links (c_id bigint not null, c_groupattribute_id bigint not null, c_grouplink_id bigint not null, c_index integer, primary key (c_id)) type=InnoDB;

alter table t_group_attributes add index FK6E5A2B82ADC57944 (c_id), add constraint FK6E5A2B82ADC57944 foreign key (c_id) references t_persistents (c_id);
alter table t_group_attributes add index FK6E5A2B822F871BC0 (c_group_id), add constraint FK6E5A2B822F871BC0 foreign key (c_group_id) references t_groups (c_id);
alter table t_groupattribute_links add index FK978FEE24BA762A0 (c_grouplink_id), add constraint FK978FEE24BA762A0 foreign key (c_grouplink_id) references t_grouplink (c_id);
alter table t_groupattribute_links add index FK978FEE2ADC57944 (c_id), add constraint FK978FEE2ADC57944 foreign key (c_id) references t_persistents (c_id);
alter table t_groupattribute_links add index FK978FEE2309478D4 (c_groupattribute_id), add constraint FK978FEE2309478D4 foreign key (c_groupattribute_id) references t_group_attributes (c_id);