CREATE TABLE ${datasource.user}.project_information (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  project_details_id NUMBER NOT NULL,
  field_stage VARCHAR2(4000),
  project_title VARCHAR2(4000),
  project_summary CLOB,
  CONSTRAINT pi_project_details_fk FOREIGN KEY (project_details_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.pi_proj_details_idx ON ${datasource.user}.project_information (project_details_id)
TABLESPACE tbsidx;