CREATE TABLE ${datasource.user}.project_assessments (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, project_quality VARCHAR2(4000)
, ready_to_be_published NUMBER
, update_required NUMBER
, assessed_datetime TIMESTAMP
, assessor_wua NUMBER
, CONSTRAINT proj_assess_proj_detail_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.proj_assess_proj_detail_idx
ON ${datasource.user}.project_assessments (project_detail_id)
TABLESPACE tbsidx;
