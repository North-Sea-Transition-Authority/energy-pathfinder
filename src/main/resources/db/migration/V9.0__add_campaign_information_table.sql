CREATE TABLE ${datasource.user}.campaign_information (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, scope_description CLOB
, published_campaign NUMBER
, CONSTRAINT campaign_information_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.campaign_information_idx ON ${datasource.user}.campaign_information (project_detail_id)
TABLESPACE tbsidx;