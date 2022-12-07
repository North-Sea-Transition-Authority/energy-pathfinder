CREATE TABLE ${datasource.user}.project_contributor (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, cont_org_group_id NUMBER NOT NULL
, CONSTRAINT proj_contr_proj_det_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.proj_contr_proj_detail_idx
  ON ${datasource.user}.project_contributor (project_detail_id)
  TABLESPACE tbsidx;