-- Patch to add a new column c_value2 to the t_long_text_values. This is
-- a CLOB with a larger size than the current c_value. We then copy the data
-- from c_value to c_value2. Requires the Hibernate mapping file Persistent.hbm.xml
-- to be modified too so that it points to c_value2, not c_value
alter table t_long_text_values add c_value2 CLOB(4096);
update t_long_text_values set c_value2=c_value;
