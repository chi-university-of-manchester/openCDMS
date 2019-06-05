
create index component_name_index on t_components (c_name);
create index date_value_index on t_date_values (c_value);
create index integer_value_index on t_integer_values (c_value);
create index numeric_value_index on t_numeric_values (c_value);
create index text_value_index on t_text_values (c_value(32));

