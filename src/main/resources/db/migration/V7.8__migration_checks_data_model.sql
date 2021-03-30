CREATE TABLE ${datasource.migration-user}.migration_check_log (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, migration_type VARCHAR2(4000) NOT NULL
, check_output CLOB
) TABLESPACE tbsdata;