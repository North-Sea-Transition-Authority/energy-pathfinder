CREATE TABLE ${datasource.user}.work_plan_awarded_contracts (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, contractor_name VARCHAR2(4000)
, contract_function VARCHAR2(4000)
, manual_contract_function VARCHAR2(4000)
, description_of_work CLOB
, date_awarded TIMESTAMP
, contract_band VARCHAR2(4000)
, contact_name VARCHAR2(4000)
, phone_number VARCHAR2(4000)
, job_title VARCHAR2(4000)
, email_address VARCHAR2(4000)
, added_by_organisation_group NUMBER NOT NULL
, CONSTRAINT work_plan_awarded_contracts_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.work_plan_awarded_contracts_idx
ON ${datasource.user}.work_plan_awarded_contracts (project_detail_id)
TABLESPACE tbsidx;
