CREATE OR REPLACE VIEW ${datasource.user}.devuk_fields AS
SELECT
  andf.field_id
, andf.field_name
, andf.operator_ou_id
, andf.status
, andf.ukcs_area
, CASE
    WHEN andf.ukcs_area = 'LAND'
      THEN 1
    ELSE 0
  END is_landward
, CASE
    WHEN andf.status = 9999
      THEN 0
    ELSE 1
  END is_active
FROM devukmgr.api_all_fields andf;