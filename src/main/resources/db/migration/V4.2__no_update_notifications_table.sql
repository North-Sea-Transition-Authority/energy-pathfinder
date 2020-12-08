CREATE TABLE ${datasource.user}.no_update_notifications (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_update_id NUMBER NOT NULL
, reason_no_update_required CLOB NOT NULL
, CONSTRAINT no_update_noti_proj_update_fk FOREIGN KEY (project_update_id) REFERENCES ${datasource.user}.project_updates (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.no_update_noti_proj_update_idx
ON ${datasource.user}.no_update_notifications (project_update_id)
TABLESPACE tbsidx;
