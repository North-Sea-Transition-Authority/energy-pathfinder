CREATE TABLE ${datasource.user}.decommissioned_wells (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, type VARCHAR2(4000)
, manual_type VARCHAR2(4000)
, number_to_be_decommissioned NUMBER
, plug_abandonment_date_quarter VARCHAR2(4000)
, plug_abandonment_date_year NUMBER
, plug_abandonment_date_type VARCHAR2(4000)
, operational_status VARCHAR2(4000)
, manual_operational_status VARCHAR2(4000)
, mechanical_status VARCHAR2(4000)
, manual_mechanical_status VARCHAR2(4000)
, CONSTRAINT decom_wells_project_detail_fk FOREIGN KEY (project_detail_id)
    REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.decom_wells_project_detail_idx
ON ${datasource.user}.decommissioned_wells (project_detail_id)
TABLESPACE tbsidx;