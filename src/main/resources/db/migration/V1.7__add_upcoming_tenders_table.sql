CREATE TABLE ${datasource.user}.upcoming_tenders (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  project_details_id NUMBER NOT NULL,
  tender_function VARCHAR2(4000),
  manual_tender_function VARCHAR2(4000),
  description_of_work CLOB,
  estimated_tender_date TIMESTAMP,
  contract_band VARCHAR2(4000),
  contact_name VARCHAR2(4000),
  phone_number VARCHAR2(4000),
  job_title VARCHAR2(4000),
  email_address VARCHAR2(4000),
  CONSTRAINT upcoming_tenders_fk FOREIGN KEY (project_details_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.pl_tenders_idx ON ${datasource.user}.upcoming_tenders (project_details_id)
TABLESPACE tbsidx;