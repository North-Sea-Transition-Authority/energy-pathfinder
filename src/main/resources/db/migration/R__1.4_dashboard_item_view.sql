CREATE OR REPLACE VIEW ${datasource.user}.dashboard_project_items AS (
  SELECT
    p.id project_id
  , pd.id project_detail_id
  , p.created_datetime
  , pd.status
  , pi.project_title
  , pi.field_stage
  , COALESCE(
      (
       SELECT f.field_name
       FROM ${datasource.user}.devuk_fields f
       WHERE f.field_id = pl.field_id
      )
    , pl.manual_field_name
    ) field_name
  , pl.ukcs_area
  , po.operator_org_grp_id
  , COALESCE(pd.submitted_datetime, pd.created_datetime, p.created_datetime) sort_key
  , pd.is_current_version
  , DECODE(
      (
       SELECT MAX(details.version)
       FROM ${datasource.user}.project_details details
       WHERE details.project_id = pd.project_id
       AND details.status IN ('QA', 'PUBLISHED', 'ARCHIVED')
      )
    , pd.version, 1
    , 0
    ) is_latest_submitted_version
  FROM ${datasource.user}.projects p
  JOIN ${datasource.user}.project_details pd ON pd.project_id = p.id
  JOIN ${datasource.user}.project_operators po ON po.project_detail_id = pd.id
  LEFT JOIN ${datasource.user}.project_information pi ON pi.project_detail_id = pd.id
  LEFT JOIN ${datasource.user}.project_locations pl ON pl.project_detail_id = pd.id
);

CREATE OR REPLACE VIEW ${datasource.user}.operator_dashboard_items AS (
  SELECT *
  FROM ${datasource.user}.dashboard_project_items dpi
  WHERE dpi.is_current_version = 1
);

CREATE OR REPLACE VIEW ${datasource.user}.regulator_dashboard_items AS (
  SELECT *
  FROM ${datasource.user}.dashboard_project_items dpi
  WHERE dpi.is_latest_submitted_version = 1
);
