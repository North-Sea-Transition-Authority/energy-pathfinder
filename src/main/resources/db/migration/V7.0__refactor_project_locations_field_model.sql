ALTER TABLE ${datasource.user}.project_locations DROP (manual_field_name, ukcs_area);

GRANT SELECT ON devukmgr.api_non_deleted_fields TO ${datasource.user};

REVOKE SELECT, REFERENCES ON devukmgr.fields FROM ${datasource.user};
