CREATE TABLE ${datasource.user}.collaboration_op_file_links(
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, opportunity_id NUMBER NOT NULL
, project_detail_file_id NUMBER NOT NULL
, CONSTRAINT co_file_link_opportunity_id_fk FOREIGN KEY (opportunity_id)
    REFERENCES ${datasource.user}.collaboration_opportunities (id)
, CONSTRAINT co_file_links_pd_file_id_fk FOREIGN KEY (project_detail_file_id)
    REFERENCES ${datasource.user}.project_detail_files (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.co_file_links_opportunity_idx
ON ${datasource.user}.collaboration_op_file_links (opportunity_id)
TABLESPACE tbsidx;

CREATE INDEX ${datasource.user}.co_file_links_pd_file_idx
ON ${datasource.user}.collaboration_op_file_links (project_detail_file_id)
TABLESPACE tbsidx;