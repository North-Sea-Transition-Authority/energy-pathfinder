CREATE TABLE ${datasource.user}.regulator_requested_updates (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_update_id NUMBER NOT NULL
, update_reason VARCHAR2(4000) NOT NULL
, deadline_date TIMESTAMP
, requested_by_wua_id NUMBER NOT NULL
, requested_datetime TIMESTAMP NOT NULL
, CONSTRAINT reg_req_update_proj_update_fk FOREIGN KEY (project_update_id) REFERENCES ${datasource.user}.project_updates (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.reg_req_update_proj_update_idx
ON ${datasource.user}.regulator_requested_updates (project_update_id)
TABLESPACE tbsidx;
