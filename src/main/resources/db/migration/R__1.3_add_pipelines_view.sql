CREATE OR REPLACE VIEW ${datasource.user}.pipelines AS
SELECT
  pd.pipeline_id id
, pd.pipeline_name name
, CASE WHEN pd.pipeline_status IN('PENDING', 'DELETED', 'LEGACY_RENUMBERED')
    THEN 1
    ELSE 0
  END historic_status
FROM pwa.api_vw_current_pipeline_data pd;
