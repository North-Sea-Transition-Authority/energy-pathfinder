CREATE TABLE ${datasource.migration-user}.water_depth_migration_mapping (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_detail_id NUMBER NOT NULL
, original_water_depth_value VARCHAR2(4000)
, sanitised_water_depth_value NUMBER
, is_migratable NUMBER NOT NULL
) TABLESPACE tbsdata;