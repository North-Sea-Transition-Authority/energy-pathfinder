CREATE TABLE ${datasource.migration-user}.location_migration_mapping (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_detail_id NUMBER NOT NULL
, original_location_value VARCHAR2(4000)
, sanitised_location_value VARCHAR2(4000)
, is_migratable NUMBER NOT NULL
) TABLESPACE tbsdata;