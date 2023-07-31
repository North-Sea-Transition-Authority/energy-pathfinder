CREATE TABLE ${datasource.user}.subscriber_field_stages (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, subscriber_uuid VARCHAR2(36) NOT NULL
, field_stage VARCHAR2(4000) NOT NULL
, CONSTRAINT subscriber_field_stages_fk FOREIGN KEY (subscriber_uuid) REFERENCES ${datasource.user}.subscribers (uuid)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.subscriber_field_stages_idx ON ${datasource.user}.subscriber_field_stages(subscriber_uuid)
TABLESPACE tbsidx;


INSERT INTO ${datasource.user}.subscriber_field_stages (subscriber_uuid, field_stage)
WITH field_stages AS (
    SELECT t.column_value field_stage
    FROM TABLE(st.split('DISCOVERY,DEVELOPMENT,DECOMMISSIONING,CARBON_CAPTURE_AND_STORAGE,HYDROGEN,OFFSHORE_ELECTRIFICATION,OFFSHORE_WIND',',')) t
)
SELECT
  s.uuid
, fs.field_stage
FROM ${datasource.user}.subscribers s
CROSS JOIN field_stages fs;

COMMIT;
