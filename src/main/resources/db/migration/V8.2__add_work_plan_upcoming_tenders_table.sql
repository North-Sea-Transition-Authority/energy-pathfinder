CREATE TABLE ${datasource.user}.work_plan_upcoming_tenders (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  project_details_id NUMBER NOT NULL,
  department_type VARCHAR2(4000),
  manual_department_type VARCHAR2(4000),
  description_of_work CLOB,
  estimated_tender_date TIMESTAMP,
  contact_name VARCHAR2(4000),
  phone_number VARCHAR2(4000),
  job_title VARCHAR2(4000),
  email_address VARCHAR2(4000),
  CONSTRAINT work_plan_upcoming_tenders_fk FOREIGN KEY (project_details_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.wp_tenders_idx ON ${datasource.user}.work_plan_upcoming_tenders (project_details_id)
TABLESPACE tbsidx;