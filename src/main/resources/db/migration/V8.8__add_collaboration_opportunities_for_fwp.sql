CREATE TABLE ${datasource.user}.work_plan_collaborations (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, function VARCHAR2(4000)
, manual_function VARCHAR2(4000)
, description_of_work CLOB
, urgent_response_needed NUMBER
, contact_name VARCHAR2(4000)
, phone_number VARCHAR2(4000)
, job_title VARCHAR2(4000)
, email_address VARCHAR2(4000)
, CONSTRAINT work_plan_collaborations_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.work_plan_collaborations_idx ON ${datasource.user}.work_plan_collaborations (project_detail_id)
TABLESPACE tbsidx;

CREATE TABLE ${datasource.user}.work_plan_collab_file_links(
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, opportunity_id NUMBER NOT NULL
, project_detail_file_id NUMBER NOT NULL
, CONSTRAINT work_plan_collab_opp_fk FOREIGN KEY (opportunity_id)
  REFERENCES ${datasource.user}.work_plan_collaborations (id)
, CONSTRAINT work_plan_collab_pd_file_id_fk FOREIGN KEY (project_detail_file_id)
  REFERENCES ${datasource.user}.project_detail_files (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.work_plan_collab_file_opp_idx
ON ${datasource.user}.work_plan_collab_file_links (opportunity_id)
TABLESPACE tbsidx;

CREATE INDEX ${datasource.user}.work_plan_collab_pd_file_idx
ON ${datasource.user}.work_plan_collab_file_links (project_detail_file_id)
TABLESPACE tbsidx;