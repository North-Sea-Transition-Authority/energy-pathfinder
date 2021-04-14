CREATE TABLE ${datasource.user}.monthly_newsletters (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, creation_date_time TIMESTAMP
, result VARCHAR2(4000)
, result_date_time TIMESTAMP
) TABLESPACE tbsdata;
