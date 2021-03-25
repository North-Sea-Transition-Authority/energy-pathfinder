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