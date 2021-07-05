CREATE OR REPLACE VIEW ${datasource.user}.published_projects AS (
  SELECT
    pd.project_id
  , pd.id project_detail_id
  FROM ${datasource.user}.project_details pd
  -- get latest published version except when an archived row exists after
  WHERE pd.status = 'PUBLISHED'
  AND pd.version = (
    SELECT MAX(d.version)
    FROM ${datasource.user}.project_details d
    WHERE d.project_id = pd.project_id
    AND d.status IN('PUBLISHED', 'ARCHIVED')
  )
);

CREATE OR REPLACE VIEW ${datasource.user}.reportable_projects AS (
  SELECT
    pp.project_detail_id
  , pp.project_id
  , pog.name operator_name
  , pi.field_stage
  , pi.project_title
  , pd.submitted_datetime last_updated_datetime
  FROM ${datasource.user}.published_projects pp
  JOIN ${datasource.user}.project_details pd ON pd.id = pp.project_detail_id
  JOIN ${datasource.user}.project_operators po ON po.project_detail_id = pp.project_detail_id
  JOIN ${datasource.user}.portal_organisation_groups pog ON pog.org_grp_id = po.operator_org_grp_id
  JOIN ${datasource.user}.project_information pi ON pi.project_detail_id = pd.id
);

CREATE OR REPLACE VIEW ${datasource.user}.api_published_projects (
  project_id
, project_type
, operator_group_name
, project_display_name
) AS
WITH all_published_projects AS (
  SELECT
    pp.project_id
  , pp.project_detail_id
  , pd.project_type
  , pog.name operator_group_name
  FROM ${datasource.user}.published_projects pp
  JOIN ${datasource.user}.project_details pd ON pd.id = pp.project_detail_id
  JOIN ${datasource.user}.project_operators po ON po.project_detail_id = pp.project_detail_id
  JOIN ${datasource.user}.portal_organisation_groups pog ON pog.org_grp_id = po.operator_org_grp_id
)
, displayable_published_projects AS (
  SELECT
    app.project_id
  , app.project_type
  , app.operator_group_name
  , CASE
      WHEN app.project_type = 'INFRASTRUCTURE' THEN
        pi.project_title || ' (' || app.operator_group_name || ')'
      WHEN app.project_type = 'FORWARD_WORK_PLAN' THEN
        'Forward work plan (' || app.operator_group_name || ')'
    END project_display_name
  FROM all_published_projects app
  LEFT JOIN ${datasource.user}.project_information pi ON pi.project_detail_id = app.project_detail_id
)
SELECT
  dpp.project_id
, dpp.project_type
, dpp.operator_group_name
, dpp.project_display_name
FROM displayable_published_projects dpp;