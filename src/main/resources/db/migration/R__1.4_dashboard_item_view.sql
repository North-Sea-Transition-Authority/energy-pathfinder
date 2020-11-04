CREATE OR REPLACE VIEW ${datasource.user}.dashboard_project_items AS (
  SELECT
    p.id project_id
  , pd.id project_detail_id
  , p.created_datetime
  , pd.status
  , pi.project_title
  , pi.field_stage
  , COALESCE(
      (SELECT f.field_name FROM ${datasource.user}.devuk_fields f WHERE f.field_id = pl.field_id)
    ,  pl.manual_field_name
    ) field_name
  , (
     SELECT pog.name
     FROM ${datasource.user}.portal_organisation_groups pog
     WHERE pog.org_grp_id = po.operator_org_grp_id
  ) operator_name
  , po.operator_org_grp_id
  FROM ${datasource.user}.projects p
  JOIN ${datasource.user}.project_details pd ON pd.project_id = p.id
  JOIN ${datasource.user}.project_operators po ON po.project_detail_id = pd.id
  LEFT JOIN ${datasource.user}.project_information pi ON pi.project_detail_id = pd.id
  LEFT JOIN ${datasource.user}.project_locations pl ON pl.project_detail_id = pd.id
  WHERE pd.is_current_version = 1
);
