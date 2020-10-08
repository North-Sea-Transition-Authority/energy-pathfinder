CREATE TABLE ${datasource.user}.platforms_fpsos (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, facility_id NUMBER
, manual_facility_name VARCHAR2(4000)
, mass NUMBER
, earliest_removal_year VARCHAR2(4000)
, latest_removal_year VARCHAR2(4000)
, substructures_to_be_removed NUMBER
, substructure_removal_premise VARCHAR2(4000)
, substructure_removal_mass NUMBER
, substructure_removal_earliest NUMBER
, substructure_removal_latest NUMBER
, fpso_type VARCHAR2(4000)
, fpso_dimensions CLOB
, future_plans VARCHAR2(4000)
, CONSTRAINT platforms_fpsos_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.platforms_fpsos_idx
ON ${datasource.user}.platforms_fpsos (project_detail_id)
TABLESPACE tbsidx;