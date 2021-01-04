CREATE TABLE ${datasource.user}.project_transfers (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, from_operator_org_grp_id NUMBER NOT NULL
, to_operator_org_grp_id NUMBER NOT NULL
, transfer_reason CLOB NOT NULL
, transferred_datetime TIMESTAMP NOT NULL
, transferred_by_wua_id NUMBER NOT NULL
, CONSTRAINT proj_transfer_proj_detail_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.proj_transfer_proj_detail_idx
ON ${datasource.user}.project_transfers (project_detail_id)
TABLESPACE tbsidx;
