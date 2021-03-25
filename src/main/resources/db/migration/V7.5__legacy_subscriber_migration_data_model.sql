GRANT SELECT ON decmgr.xview_resource_people_history TO ${datasource.migration-user};

GRANT SELECT ON decmgr.newsletter_recipients TO ${datasource.migration-user};

GRANT SELECT, INSERT ON ${datasource.user}.subscribers TO ${datasource.migration-user};

CREATE TABLE ${datasource.migration-user}.subscriber_migration_log (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_resource_person_id NUMBER NOT NULL
, migration_status VARCHAR2(4000) NOT NULL
, system_message CLOB
, new_subscriber_id NUMBER
) TABLESPACE tbsdata;