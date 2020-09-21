CREATE TABLE ${datasource.user}.uploaded_files (
  file_id VARCHAR2(4000) NOT NULL  PRIMARY KEY
, file_name VARCHAR2(4000) NOT NULL
, file_data BLOB
, content_type VARCHAR2(4000)
, file_size NUMBER
, uploaded_by_wua_id NUMBER
, last_updated_by_wua_id NUMBER
, status VARCHAR2(10)
, upload_datetime TIMESTAMP
, CONSTRAINT uploaded_files_ck1 CHECK (status IN ('CURRENT', 'DELETED'))
) TABLESPACE tbsdata;

CREATE TABLE ${datasource.user}.project_detail_files(
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, file_id VARCHAR2(4000) NOT NULL
, description VARCHAR2(4000)
, purpose VARCHAR2(4000) NOT NULL
, file_link_status VARCHAR2(4000) NOT NULL
, CONSTRAINT pd_files_project_detail_id_fk FOREIGN KEY (project_detail_id)
    REFERENCES ${datasource.user}.project_details (id)
, CONSTRAINT pd_files_file_id_fk FOREIGN KEY (file_id)
    REFERENCES ${datasource.user}.uploaded_files (file_id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.pd_files_project_detail_id_idx
ON ${datasource.user}.project_detail_files (project_detail_id)
TABLESPACE tbsidx;

CREATE INDEX ${datasource.user}.pd_files_file_id_idx
ON ${datasource.user}.project_detail_files (file_id)
TABLESPACE tbsidx;