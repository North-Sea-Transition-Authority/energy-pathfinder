CREATE TABLE ${datasource.user}.commissioned_well_schedules (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, earliest_start_year NUMBER
, latest_completion_year NUMBER
, CONSTRAINT commission_well_proj_detail_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.commission_well_schedule_idx1
ON ${datasource.user}.commissioned_well_schedules (project_detail_id)
TABLESPACE tbsidx;

CREATE TABLE ${datasource.user}.commissioned_wells (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, commissioned_well_schedule_id NUMBER NOT NULL
, wellbore_id NUMBER NOT NULL
, CONSTRAINT commission_well_schedule_fk FOREIGN KEY (commissioned_well_schedule_id) REFERENCES ${datasource.user}.commissioned_well_schedules (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.commission_well_schedule_idx
ON ${datasource.user}.commissioned_wells (commissioned_well_schedule_id)
TABLESPACE tbsidx;