CREATE TABLE ${datasource.user}.projects (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  created_datetime TIMESTAMP
) TABLESPACE tbsdata;

CREATE TABLE ${datasource.user}.project_details (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  project_id NUMBER NOT NULL,
  status VARCHAR2(4000) NOT NULL,
  version NUMBER NOT NULL,
  is_current_version NUMBER NOT NULL,
  created_by_wua NUMBER NOT NULL,
  CONSTRAINT pd_project_fk FOREIGN KEY (project_id) REFERENCES ${datasource.user}.projects (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.pd_projects_idx ON ${datasource.user}.project_details (project_id)
TABLESPACE tbsidx;


CREATE TABLE ${datasource.user}.project_operators (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  project_details_id NUMBER NOT NULL,
  operator_org_grp_id NUMBER NOT NULL,
  CONSTRAINT pd_operator_id_fk FOREIGN KEY (project_details_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.project_operators_idx1 ON ${datasource.user}.project_operators (project_details_id)
TABLESPACE tbsidx;