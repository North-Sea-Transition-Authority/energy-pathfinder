CREATE TABLE ${datasource.user}.awarded_contracts (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_details_id NUMBER NOT NULL
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
, CONSTRAINT awarded_contracts_fk FOREIGN KEY (project_details_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.awarded_contracts_idx
ON ${datasource.user}.awarded_contracts (project_details_id)
TABLESPACE tbsidx;