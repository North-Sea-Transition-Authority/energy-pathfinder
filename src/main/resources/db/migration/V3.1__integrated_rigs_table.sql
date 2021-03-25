CREATE TABLE ${datasource.user}.integrated_rigs (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, facility_id NUMBER
, manual_facility VARCHAR2(4000)
, name VARCHAR2(4000)
, status VARCHAR2(4000)
, intention_to_reactivate VARCHAR2(4000)
, CONSTRAINT int_rig_project_detail_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.int_rig_proj_detail_idx
ON ${datasource.user}.integrated_rigs (project_detail_id)
TABLESPACE tbsidx;
