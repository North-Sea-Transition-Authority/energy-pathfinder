CREATE TABLE ${datasource.user}.service_feedback (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, rating VARCHAR2(4000) NOT NULL
, feedback CLOB
, project_detail_id NUMBER
, submitter_name VARCHAR2(4000) NOT NULL
, submitter_email_address VARCHAR2(4000) NOT NULL
, submitted_datetime TIMESTAMP NOT NULL
) TABLESPACE tbsdata;
