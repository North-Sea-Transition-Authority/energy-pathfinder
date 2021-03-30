CREATE OR REPLACE VIEW ${datasource.user}.devuk_fields AS
SELECT
  andf.field_id
, andf.field_name
, andf.operator_ou_id
, andf.status
, andf.ukcs_area
FROM devukmgr.api_non_deleted_fields andf
WHERE (andf.ukcs_area != 'LAND' OR andf.ukcs_area IS NULL);
