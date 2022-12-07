CREATE TABLE ${datasource.user}.work_plan_contributor_details (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, has_project_contributors NUMBER
, CONSTRAINT fwd_proj_contr_proj_det_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.fwd_proj_contr_proj_detail_idx
  ON ${datasource.user}.work_plan_contributor_details (project_detail_id)
  TABLESPACE tbsidx;