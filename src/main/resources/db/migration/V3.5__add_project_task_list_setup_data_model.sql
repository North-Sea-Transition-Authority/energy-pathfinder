CREATE TABLE ${datasource.user}.project_task_list_setup (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, project_detail_id NUMBER NOT NULL
, task_list_sections CLOB
, task_list_answers CLOB
, CONSTRAINT project_task_list_setup_fk FOREIGN KEY (project_detail_id) REFERENCES ${datasource.user}.project_details (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.project_task_list_setup_idx
ON ${datasource.user}.project_task_list_setup (project_detail_id)
TABLESPACE tbsidx;
