CREATE TABLE ${datasource.user}.subsea_infrastructure (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, facility_id NUMBER
, manual_facility VARCHAR2(4000)
, description CLOB
, status VARCHAR2(4000)
, infrastructure_type VARCHAR2(400)
, number_of_mattresses NUMBER
, total_estimated_mattress_mass NUMBER
, total_estimated_subsea_mass VARCHAR2(4000)
, other_infrastructure_type VARCHAR2(4000)
, total_estimated_other_mass NUMBER
, earliest_decom_start_year NUMBER
, latest_decom_completion_year NUMBER
, CONSTRAINT subsea_infra_project_detail_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.subsea_infra_proj_detail_idx
ON ${datasource.user}.subsea_infrastructure (project_detail_id)
TABLESPACE tbsidx;