ALTER TABLE ${datasource.user}.project_locations ADD (
  ukcs_area VARCHAR2(4000)
);

CREATE TABLE ${datasource.user}.project_location_blocks (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  project_location_id NUMBER NOT NULL,
  plm_id NUMBER,
  block_ref VARCHAR2(4000),
  block_no VARCHAR2(4000),
  quadrant_no VARCHAR2(4000),
  block_suffix VARCHAR2(4000),
  location VARCHAR2(4000),
  CONSTRAINT plb_project_locations_fk FOREIGN KEY (project_location_id) REFERENCES ${datasource.user}.project_locations (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.plb_project_locations_idx ON ${datasource.user}.project_location_blocks (project_location_id)
TABLESPACE tbsidx;
