GRANT SELECT ON devukmgr.field_operator_view TO ${datasource.user};
GRANT REFERENCES ON devukmgr.field_operator_view TO ${datasource.user};
GRANT SELECT ON devukmgr.fields TO ${datasource.user};
GRANT REFERENCES ON devukmgr.fields TO ${datasource.user};

CREATE OR REPLACE VIEW ${datasource.user}.devuk_fields AS
SELECT
  f.field_identifier field_id
, f.name field_name
, fov.operator_id operator_ou_id
, f.status
FROM devukmgr.fields f
LEFT JOIN devukmgr.field_operator_view fov ON f.field_identifier = fov.field_id;