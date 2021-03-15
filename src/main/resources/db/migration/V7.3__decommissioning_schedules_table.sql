CREATE TABLE ${datasource.user}.decommissioning_schedules (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, start_date_type VARCHAR2(4000)
, exact_start_date TIMESTAMP
, estimated_start_date_quarter VARCHAR2(4000)
, estimated_start_date_year NUMBER
, start_date_not_provided_reason CLOB
, cop_date_type VARCHAR2(4000)
, exact_cop_date TIMESTAMP
, estimated_cop_date_quarter VARCHAR2(4000)
, estimated_cop_date_year NUMBER
, cop_date_not_provided_reason CLOB
, CONSTRAINT decom_schedules_proj_detail_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.decom_schedule_proj_detail_idx
ON ${datasource.user}.decommissioning_schedules (project_detail_id)
TABLESPACE tbsidx;
