
--
-- This file contains sql for creating indexes that cannot be generated via xdoclet
-- See http://dev.mysql.com/doc/refman/5.0/en/create-index.html
--

--
-- Create a 32 character prefix index on text values
--
create index text_value_index on t_text_values (c_value(32));