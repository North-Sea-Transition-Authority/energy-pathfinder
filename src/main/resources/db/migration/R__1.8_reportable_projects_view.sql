CREATE OR REPLACE VIEW ${datasource.user}.reportable_projects AS (
  SELECT
    pd.id project_detail_id
  , pd.project_id
  , pog.name operator_name
  , pi.field_stage
  , pi.project_title
  , pd.submitted_datetime last_updated_datetime
  , NULL updated_in_current_quarter
  FROM ${datasource.user}.project_details pd
  JOIN ${datasource.user}.project_operators po ON po.project_detail_id = pd.id
  JOIN ${datasource.user}.portal_organisation_groups pog ON pog.org_grp_id = po.operator_org_grp_id
  JOIN ${datasource.user}.project_information pi ON pi.project_detail_id = pd.id
  -- get last submitted version which is published
  WHERE pd.status = 'PUBLISHED'
  AND pd.version = (
    SELECT MAX(d.version)
    FROM ${datasource.user}.project_details d
    WHERE d.project_id = pd.project_id
    AND d.status IN('QA', 'PUBLISHED', 'ARCHIVED')
  )
);