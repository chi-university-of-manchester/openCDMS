
create index dob_index on t_subjects (c_date_of_birth);
create index nhs_number_index on t_subjects (c_nhs_number);
create index mobile_phone_index on t_subjects (c_mobile_phone);
create index name_index on t_subjects (c_first_name, c_last_name);
