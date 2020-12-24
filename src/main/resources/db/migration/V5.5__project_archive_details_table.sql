CREATE TABLE ${datasource.user}.project_archive_details (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, archive_reason CLOB NOT NULL
, CONSTRAINT proj_archive_details_proj_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.proj_archive_details_proj_idx
ON ${datasource.user}.project_archive_details (project_detail_id)
TABLESPACE tbsidx;
