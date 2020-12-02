CREATE TABLE ${datasource.user}.project_updates (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, from_project_detail_id NUMBER NOT NULL
, new_project_detail_id NUMBER NOT NULL
, update_type VARCHAR2(4000)
, CONSTRAINT proj_assess_from_detail_fk FOREIGN KEY (from_project_detail_id) REFERENCES ${datasource.user}.project_details (id)
, CONSTRAINT proj_assess_new_detail_fk FOREIGN KEY (new_project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.proj_update_from_detail_idx
ON ${datasource.user}.project_updates (from_project_detail_id)
TABLESPACE tbsidx;

CREATE INDEX ${datasource.user}.proj_update_new_detail_idx
ON ${datasource.user}.project_updates (new_project_detail_id)
TABLESPACE tbsidx;
