CREATE TABLE ${datasource.user}.decommissioned_pipelines (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, pipeline_id NUMBER
, material_type VARCHAR2(4000)
, status VARCHAR2(4000)
, earliest_removal_year VARCHAR2(4000)
, latest_removal_year VARCHAR2(4000)
, removal_premise VARCHAR2(4000)
, CONSTRAINT pipeline_project_detail_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.pipeline_proj_detail_idx
ON ${datasource.user}.decommissioned_pipelines (project_detail_id)
TABLESPACE tbsidx;
