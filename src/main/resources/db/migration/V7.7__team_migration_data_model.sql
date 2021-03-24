GRANT SELECT ON securemgr.web_user_accounts TO ${datasource.migration-user};

GRANT SELECT ON decmgr.resource_members_current TO ${datasource.migration-user};

GRANT EXECUTE ON decmgr.contact TO ${datasource.migration-user};

GRANT SELECT ON decmgr.resources TO ${datasource.migration-user};

GRANT SELECT ON decmgr.current_organisation_groups TO ${datasource.migration-user};

GRANT SELECT ON decmgr.resource_usages_current TO ${datasource.migration-user};

GRANT SELECT ON decmgr.xview_resources TO ${datasource.migration-user};

CREATE TABLE ${datasource.migration-user}.team_migration_log (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_resource_id NUMBER NOT NULL
, migration_status VARCHAR2(4000) NOT NULL
, system_message CLOB
, new_resource_id NUMBER
) TABLESPACE tbsdata;