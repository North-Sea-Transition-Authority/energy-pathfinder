ALTER TABLE ${datasource.user}.regulator_requested_updates
RENAME TO regulator_update_requests;

ALTER TABLE ${datasource.user}.regulator_update_requests
ADD project_detail_id NUMBER;

UPDATE ${datasource.user}.regulator_update_requests ror
SET ror.project_detail_id = (
  SELECT from_project_detail_id
  FROM ${datasource.user}.project_updates pu
  WHERE pu.id = ror.project_update_id
);

ALTER TABLE ${datasource.user}.regulator_update_requests
ADD CONSTRAINT reg_update_request_proj_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id);

ALTER TABLE ${datasource.user}.regulator_update_requests
MODIFY project_detail_id NOT NULL;

ALTER TABLE ${datasource.user}.regulator_update_requests
DROP COLUMN project_update_id;
