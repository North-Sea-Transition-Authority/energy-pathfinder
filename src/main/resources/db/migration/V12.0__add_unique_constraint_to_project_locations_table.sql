DELETE FROM ${datasource.user}.project_locations
WHERE id NOT IN (
  SELECT MIN(id)
  FROM ${datasource.user}.project_locations
  GROUP BY project_detail_id
);

ALTER TABLE ${datasource.user}.project_locations
ADD CONSTRAINT project_locations_project_detail_id_unq UNIQUE (project_detail_id);
