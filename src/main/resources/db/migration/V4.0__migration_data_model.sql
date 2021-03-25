CREATE TABLE ${datasource.migration-user}.project_migration_log (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_id NUMBER
, migration_status VARCHAR2(4000)
, system_message CLOB
, new_project_id NUMBER
) TABLESPACE tbsdata;

CREATE TABLE ${datasource.migration-user}.project_detail_migration_log (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_id NUMBER
, legacy_project_detail_id NUMBER
, migration_status VARCHAR2(4000)
, system_message CLOB
, new_project_detail_id NUMBER
) TABLESPACE tbsdata;

GRANT SELECT ON decmgr.path_projects TO ${datasource.migration-user};

GRANT SELECT ON decmgr.path_project_details TO ${datasource.migration-user};

GRANT SELECT, INSERT ON ${datasource.user}.projects TO ${datasource.migration-user};

GRANT SELECT, INSERT ON ${datasource.user}.project_details TO ${datasource.migration-user};

GRANT SELECT, INSERT ON ${datasource.user}.project_publishing_details TO ${datasource.migration-user};