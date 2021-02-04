CREATE TABLE ${datasource.user}.communications (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, recipient_type VARCHAR2(4000)
, email_subject VARCHAR2(4000)
, email_body CLOB
, status VARCHAR2(4000) NOT NULL
, created_by_wua_id NUMBER NOT NULL
, created_datetime TIMESTAMP NOT NULL
, submitted_by_wua_id NUMBER
, submitted_datetime TIMESTAMP
, latest_journey_status VARCHAR2(4000) NOT NULL
) TABLESPACE tbsdata;

CREATE TABLE ${datasource.user}.org_group_communications (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, communication_id NUMBER NOT NULL
, organisation_group_id NUMBER NOT NULL
, CONSTRAINT org_group_communications_fk FOREIGN KEY (communication_id) REFERENCES ${datasource.user}.communications (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.org_group_communications_idx
ON ${datasource.user}.org_group_communications (communication_id)
TABLESPACE tbsidx;