CREATE TABLE ${datasource.user}.upcoming_tender_file_links(
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, upcoming_tender_id NUMBER NOT NULL
, project_detail_file_id NUMBER NOT NULL
, CONSTRAINT ut_file_links_tender_id_fk FOREIGN KEY (upcoming_tender_id)
    REFERENCES ${datasource.user}.upcoming_tenders (id)
, CONSTRAINT ut_file_links_pd_file_id_fk FOREIGN KEY (project_detail_file_id)
    REFERENCES ${datasource.user}.project_detail_files (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.ut_file_links_tender_idx
ON ${datasource.user}.upcoming_tender_file_links (upcoming_tender_id)
TABLESPACE tbsidx;

CREATE INDEX ${datasource.user}.ut_file_links_pd_file_idx
ON ${datasource.user}.upcoming_tender_file_links (project_detail_file_id)
TABLESPACE tbsidx;