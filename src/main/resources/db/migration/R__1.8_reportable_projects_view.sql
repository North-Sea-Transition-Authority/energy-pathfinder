CREATE OR REPLACE VIEW ${datasource.user}.reportable_projects AS (
  SELECT
    pd.id project_detail_id
  , pi.field_stage
  , pd.submitted_datetime last_updated_datetime
  FROM ${datasource.user}.project_details pd
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