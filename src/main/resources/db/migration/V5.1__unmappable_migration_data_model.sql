CREATE TABLE ${datasource.migration-user}.unmapped_project_questions (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_question_mnem VARCHAR2(4000) NOT NULL
, legacy_question_text VARCHAR2(4000) NOT NULL
, legacy_hint_text CLOB
) TABLESPACE tbsdata;

CREATE TABLE ${datasource.migration-user}.unmapped_project_data (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_id NUMBER NOT NULL
, new_project_id NUMBER NOT NULL
, legacy_project_detail_id NUMBER NOT NULL
, new_project_detail_id NUMBER NOT NULL
, legacy_question_id NUMBER NOT NULL
, legacy_answer VARCHAR2(4000) NOT NULL
, CONSTRAINT unmapped_project_data_fk FOREIGN KEY (legacy_question_id) REFERENCES ${datasource.migration-user}.unmapped_project_questions (id)
) TABLESPACE tbsdata;

CREATE TABLE ${datasource.migration-user}.unmapped_project_comments (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_id NUMBER NOT NULL
, comment_text CLOB NOT NULL
, comment_datetime TIMESTAMP NOT NULL
, commented_by_wua_id NUMBER NOT NULL
, comment_type VARCHAR2(4000) NOT NULL
) TABLESPACE tbsdata;

CREATE TABLE ${datasource.migration-user}.unmapped_project_migration_log (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_id NUMBER
, migration_status VARCHAR2(4000)
, system_message CLOB
) TABLESPACE tbsdata;

CREATE TABLE ${datasource.migration-user}.unmapped_detail_migration_log (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_id NUMBER
, legacy_project_detail_id NUMBER
, migration_status VARCHAR2(4000)
, system_message CLOB
) TABLESPACE tbsdata;