CREATE OR REPLACE VIEW ${datasource.user}.pipelines AS
SELECT
  NULL id
, NULL name
FROM dual
WHERE 1=2
CONNECT BY LEVEL <= 10;
