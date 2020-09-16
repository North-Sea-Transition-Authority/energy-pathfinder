CREATE TABLE ${datasource.user}.collaboration_opportunities (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  project_details_id NUMBER NOT NULL,
  function VARCHAR2(4000),
  manual_function VARCHAR2(4000),
  description_of_work CLOB,
  estimated_service_date TIMESTAMP,
  contract_band VARCHAR2(4000),
  contact_name VARCHAR2(4000),
  phone_number VARCHAR2(4000),
  job_title VARCHAR2(4000),
  email_address VARCHAR2(4000),
  CONSTRAINT collaboration_opportunities_fk FOREIGN KEY (project_details_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.collab_opportunities_idx ON ${datasource.user}.collaboration_opportunities (project_details_id)
TABLESPACE tbsidx;