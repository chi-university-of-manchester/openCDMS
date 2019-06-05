-- Patch to add the t_rule_assocrules table, used to persist the ValidationRule.associatedRules list
-- This patch should be applied to all databases deployed from revisions prior to Rev. 2901
create table t_rule_assocrules (c_rule_id bigint not null, c_assoc_rule_id bigint not null, c_index integer not null, primary key (c_rule_id, c_index));
alter table t_rule_assocrules add constraint FKDD61595AF31DA9C0 foreign key (c_rule_id) references t_val_rules;
alter table t_rule_assocrules add constraint FKDD61595A274CAC96 foreign key (c_assoc_rule_id) references t_val_rules;
