DROP INDEX project_details_one_current_version_unq_idx;

CREATE UNIQUE INDEX ${datasource.user}.project_details_one_current_version_unq_idx
  ON ${datasource.user}.project_details(CASE WHEN is_current_version = 1 THEN project_id END);