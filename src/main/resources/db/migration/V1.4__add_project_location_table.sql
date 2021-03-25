CREATE TABLE ${datasource.user}.project_locations (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  project_details_id NUMBER NOT NULL,
  field_id NUMBER,
  manual_field_name VARCHAR2(4000),
  CONSTRAINT pl_project_details_fk FOREIGN KEY (project_details_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.pl_proj_details_idx ON ${datasource.user}.project_locations (project_details_id)
TABLESPACE tbsidx;