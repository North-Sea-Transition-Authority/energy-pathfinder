CREATE TABLE ${datasource.user}.project_publishing_details (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, published_datetime TIMESTAMP
, publisher_wua_id NUMBER
, CONSTRAINT proj_pub_details_project_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.proj_pub_details_project_idx
ON ${datasource.user}.project_publishing_details (project_id)
TABLESPACE tbsidx;
