CREATE TABLE ${datasource.user}.work_plan_awarded_contracts_setup (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, has_contract_to_add NUMBER
, has_other_contract_to_add NUMBER
, CONSTRAINT work_plan_contracts_setup_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.work_plan_contracts_setup_idx ON ${datasource.user}.work_plan_awarded_contracts_setup(project_detail_id)
TABLESPACE tbsidx;
