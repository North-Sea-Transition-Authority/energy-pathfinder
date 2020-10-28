CREATE OR REPLACE VIEW ${datasource.user}.pipelines AS
SELECT
  rownum id
, 'Pipeline ' || rownum name
FROM dual
CONNECT BY LEVEL <= 10;
