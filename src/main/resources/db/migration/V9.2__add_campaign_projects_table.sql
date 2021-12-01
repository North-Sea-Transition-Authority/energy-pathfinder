CREATE TABLE ${datasource.user}.campaign_projects (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, campaign_id NUMBER NOT NULL
, project_id NUMBER NOT NULL
, CONSTRAINT campaign_projects_campaign_fk FOREIGN KEY (campaign_id) REFERENCES ${datasource.user}.campaign_information (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.campaign_projects_campaign_idx
ON ${datasource.user}.campaign_projects (campaign_id)
TABLESPACE tbsidx;