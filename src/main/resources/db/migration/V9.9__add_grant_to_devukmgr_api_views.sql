GRANT SELECT ON devukmgr.api_all_fields TO ${datasource.user} WITH GRANT OPTION;

REVOKE SELECT ON devukmgr.api_non_deleted_fields FROM ${datasource.user};