GRANT SELECT, INSERT ON ${datasource.user}.project_operators TO ${datasource.migration-user};

GRANT SELECT, INSERT ON ${datasource.user}.project_information TO ${datasource.migration-user};

GRANT SELECT, INSERT ON ${datasource.user}.project_locations TO ${datasource.migration-user};

GRANT SELECT ON devukmgr.fields TO ${datasource.migration-user};

GRANT SELECT, INSERT ON ${datasource.user}.awarded_contracts TO ${datasource.migration-user};

GRANT SELECT, INSERT ON ${datasource.user}.collaboration_opportunities TO ${datasource.migration-user};

GRANT SELECT, INSERT ON ${datasource.user}.project_task_list_setup TO ${datasource.migration-user};