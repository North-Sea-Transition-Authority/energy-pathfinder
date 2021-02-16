CREATE TABLE ${datasource.user}.plug_abandonment_schedules (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, earliest_start_year NUMBER
, latest_completion_year NUMBER
, CONSTRAINT pa_schedule_proj_detail_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.pa_schedule_proj_detail_idx
ON ${datasource.user}.plug_abandonment_schedules (project_detail_id)
TABLESPACE tbsidx;

GRANT SELECT ON wellmgr.api_extant_wellbores TO ${datasource.user};

CREATE TABLE ${datasource.user}.plug_abandonment_wells (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, plug_abandonment_schedule_id NUMBER NOT NULL
, wellbore_id NUMBER NOT NULL
, CONSTRAINT pa_schedule_well_pas_fk FOREIGN KEY (plug_abandonment_schedule_id) REFERENCES ${datasource.user}.plug_abandonment_schedules (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.pa_schedule_well_pas_idx
ON ${datasource.user}.plug_abandonment_wells (plug_abandonment_schedule_id)
TABLESPACE tbsidx;
