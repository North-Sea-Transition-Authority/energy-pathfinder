CREATE TABLE pathfinder_migration.project_migration_log (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_id NUMBER
, migration_status VARCHAR2(4000)
, system_message CLOB
, new_project_id NUMBER
) TABLESPACE tbsdata;

CREATE TABLE pathfinder_migration.project_detail_migration_log (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_id NUMBER
, legacy_project_detail_id NUMBER
, migration_status VARCHAR2(4000)
, system_message CLOB
, new_project_detail_id NUMBER
) TABLESPACE tbsdata;

CREATE OR REPLACE VIEW pathfinder_migration.legacy_project_data AS (
  SELECT
    ppd.path_project_id legacy_project_id
  , ppd.id legacy_project_detail_id
  , po.org_grp_id operator_org_group_id
  , xt.field_stage
  , xt.project_title
  , xt.manual_field_name
  , envmgr.st.to_number_safe(xt.devuk_field_id) devuk_field_id
  , xt.field_type
  , DECODE(xt.fdp_approved, 'true', 1, 0) fdp_approved
  , xt.first_production_quarter
  , envmgr.st.to_number_safe(xt.first_production_year) first_production_year
  , xt.project_summary
  , xt.project_contact_name
  , xt.project_contact_job_title
  , xt.project_contact_email_address
  , xt.project_contact_tel_number
  , xt.water_depth
  , xt.location
  FROM decmgr.path_project_details ppd
  CROSS JOIN XMLTABLE('PROJECT_DETAIL'
    PASSING ppd.xml_data
    COLUMNS
      path_operator_id VARCHAR2(4000) PATH 'PATH_OPERATOR_ID/text()'
    , field_stage VARCHAR2(4000) PATH 'PROJECT_TYPE/text()'
    , project_title VARCHAR2(4000) PATH 'PROJECT_TITLE/text()'
    , manual_field_name VARCHAR2(4000) PATH 'FIELD_NAME/text()'
    , devuk_field_id VARCHAR2(4000) PATH 'DEVUK_FIELD_ID/text()'
    , field_type VARCHAR2(4000) PATH 'FIELD_TYPE/text()'
    , fdp_approved VARCHAR2(4000) PATH 'FDP_APPROVED_FLAG/text()'
    , first_production_quarter VARCHAR2(4000) PATH 'FIRST_PRODUCTION_QUARTER/text()'
    , first_production_year VARCHAR2(4000) PATH 'FIRST_PRODUCTION_YEAR/text()'
    , project_summary CLOB PATH 'PROJECT_SUMMARY/text()'
    , project_contact_name VARCHAR2(4000) PATH 'CONTACT_NAME/text()'
    , project_contact_job_title VARCHAR2(4000) PATH 'CONTACT_JOB_TITLE/text()'
    , project_contact_email_address VARCHAR2(4000) PATH 'CONTACT_EMAIL_ADDRESS/text()'
    , project_contact_tel_number VARCHAR2(4000) PATH 'CONTACT_TELEPHONE_NUMBER/text()'
    , water_depth VARCHAR2(4000) PATH 'WATER_DEPTH/text()'
    , location VARCHAR2(4000) PATH 'LOCATION/text()'
  ) xt
  LEFT JOIN decmgr.path_operators po ON po.id = envmgr.st.to_number_safe(xt.path_operator_id)
);

CREATE OR REPLACE VIEW pathfinder_migration.legacy_project_challenges AS (
  SELECT
    ppd.path_project_id legacy_project_id
  , ppd.id legacy_project_detail_id
  , xt.description_of_work
  , xt.contact_name
  , xt.contact_telephone_no
  , xt.contact_email_address
  FROM decmgr.path_project_details ppd
  , XMLTABLE('PROJECT_DETAIL/CHALLENGE_LIST/CHALLENGE'
    PASSING ppd.xml_data
    COLUMNS
      description_of_work CLOB PATH 'DESCRIPTION/text()'
    , contact_name VARCHAR2(4000) PATH 'CONTACT_NAME/text()'
    , contact_telephone_no VARCHAR2(4000) PATH 'TELEPHONE_NUMBER/text()'
    , contact_email_address VARCHAR2(4000) PATH 'EMAIL_ADDRESS/text()'
  ) xt
);

CREATE OR REPLACE VIEW pathfinder_migration.legacy_project_contracts AS (
  SELECT
    ppd.path_project_id legacy_project_id
  , ppd.id legacy_project_detail_id
  , xt.description_of_work
  , xt.contractor_name
  , xt.contact_name
  , envmgr.dt.to_date_safe(xt.date_awarded) date_awarded
  , xt.contract_band
  , xt.contact_telephone_no
  , xt.contact_email_address
  FROM decmgr.path_project_details ppd
  , XMLTABLE('PROJECT_DETAIL/CONTRACT_LIST/CONTRACT'
    PASSING ppd.xml_data
    COLUMNS
      description_of_work CLOB PATH 'CONTRACT_TITLE/text()'
    , contractor_name VARCHAR2(4000) PATH 'CONTRACTOR_NAME/text()'
    , contact_name VARCHAR2(4000) PATH 'PRIMARY_CONTACT/text()'
    , date_awarded VARCHAR2(4000) PATH 'DATE_AWARDED/text()'
    , contract_band VARCHAR2(4000) PATH 'CONTRACT_BAND/text()'
    , contact_telephone_no VARCHAR2(4000) PATH 'TELEPHONE_NUMBER/text()'
    , contact_email_address VARCHAR2(4000) PATH 'EMAIL_ADDRESS/text()'
  ) xt
);

CREATE OR REPLACE FORCE VIEW pathfinder_migration.legacy_subscribers AS (
  SELECT
    nr.resource_person_id
  , xrph.forename
  , xrph.surname
  , xrph.portal_email_address email_address
  FROM decmgr.newsletter_recipients nr
  JOIN decmgr.xview_resource_people_history xrph ON xrph.rp_id = nr.resource_person_id AND xrph.status_control = 'C'
  WHERE nr.newsletter_type = 'PATHFINDER'
  AND nr.status = 'SUBSCRIBED'
);

CREATE OR REPLACE FORCE VIEW pathfinder_migration.legacy_teams AS (
  -- regulator team
  SELECT
    r.id legacy_resource_id
  , xr.res_type resource_type
  , NULL scoped_uref
  FROM decmgr.resources r
  JOIN decmgr.xview_resources xr ON xr.res_id = r.id
  WHERE r.res_type = 'PATH_ADMIN_TEAM'
  UNION
  -- organisation teams
  SELECT
    ruc.res_id
  , xr.res_type
  , ruc.uref
  FROM decmgr.resource_usages_current ruc
  JOIN decmgr.current_organisation_groups cog ON cog.id || '++REGORGGRP' = ruc.uref
  JOIN decmgr.xview_resources xr ON xr.res_id = ruc.res_id
  WHERE cog.org_grp_type = 'REG'
  AND xr.res_type = 'PATH_OPERATOR_TEAM'
);

CREATE TABLE pathfinder_migration.unmapped_legacy_project_data AS (
  SELECT
    ppd.path_project_id legacy_project_id
  , ppd.id legacy_project_detail_id
  , xt.update_summary
  , xt.original_production_quarter
  , xt.original_production_year
  , xt.under_construction_flag
  , xt.construction_date
  FROM decmgr.path_project_details ppd
  , XMLTABLE('PROJECT_DETAIL'
    PASSING ppd.xml_data
    COLUMNS
      update_summary VARCHAR2(4000) PATH 'UPDATE_SUMMARY/text()'
    , original_production_quarter VARCHAR2(4000) PATH 'ORIGINAL_PRODUCTION_QUARTER/text()'
    , original_production_year VARCHAR2(4000) PATH 'ORIGINAL_PRODUCTION_YEAR/text()'
    , under_construction_flag VARCHAR2(4000) PATH 'UNDER_CONSTRUCTION_FLAG/text()'
    , construction_date VARCHAR2(4000) PATH 'CONSTRUCTION_DATE/text()'
  ) xt
);

CREATE TABLE pathfinder_migration.unmapped_legacy_comments AS (
  SELECT
    pp.id legacy_project_id
  , xt.comment_text
  , envmgr.dt.to_datetime_safe(xt.comment_datetime) comment_datetime
  , envmgr.st.to_number_safe(xt.commented_by_wua_id) commented_by_wua_id
  , CASE
      WHEN xt.comment_type = 'DECC_TO_OPERATOR_COMMENTS' THEN 'REGULATOR_TO_OPERATOR'
      WHEN xt.comment_type = 'DECC_TO_DECC_COMMENTS' THEN 'REGULATOR_TO_REGULATOR'
    END comment_type
  FROM decmgr.path_projects pp
  , XMLTABLE('COMMENTS/(DECC_TO_DECC_COMMENTS, DECC_TO_OPERATOR_COMMENTS)/COMMENT_LIST/COMMENT'
    PASSING pp.comments_xml
    COLUMNS
      comment_text VARCHAR2(4000) PATH 'TEXT/text()'
    , comment_datetime VARCHAR2(4000) PATH 'DATETIME/text()'
    , commented_by_wua_id VARCHAR2(4000) PATH 'WUA_ID/text()'
    , comment_type VARCHAR2(4000) PATH 'ancestor-or-self::COMMENT_LIST/../name()'
  ) xt
);

CREATE TABLE pathfinder_migration.subscriber_migration_log (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_resource_person_id NUMBER NOT NULL
, migration_status VARCHAR2(4000) NOT NULL
, system_message CLOB
, new_subscriber_id NUMBER
) TABLESPACE tbsdata;

CREATE TABLE pathfinder_migration.team_migration_log (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_resource_id NUMBER NOT NULL
, migration_status VARCHAR2(4000) NOT NULL
, system_message CLOB
, new_resource_id NUMBER
) TABLESPACE tbsdata;

CREATE TABLE pathfinder_migration.migration_check_log (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, migration_type VARCHAR2(4000) NOT NULL
, check_output CLOB
) TABLESPACE tbsdata;

CREATE TABLE pathfinder_migration.location_migration_mapping (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_detail_id NUMBER NOT NULL
, original_location_value VARCHAR2(4000)
, sanitised_location_value VARCHAR2(4000)
, is_migratable NUMBER NOT NULL
) TABLESPACE tbsdata;

CREATE TABLE pathfinder_migration.water_depth_migration_mapping (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_detail_id NUMBER NOT NULL
, original_water_depth_value VARCHAR2(4000)
, sanitised_water_depth_value NUMBER
, is_migratable NUMBER NOT NULL
) TABLESPACE tbsdata;

CREATE TABLE pathfinder_migration.unmapped_project_questions (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_question_mnem VARCHAR2(4000) NOT NULL
, legacy_question_text VARCHAR2(4000) NOT NULL
, legacy_hint_text CLOB
) TABLESPACE tbsdata;

CREATE TABLE pathfinder_migration.unmapped_project_data (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_id NUMBER NOT NULL
, new_project_id NUMBER NOT NULL
, legacy_project_detail_id NUMBER NOT NULL
, new_project_detail_id NUMBER NOT NULL
, legacy_question_id NUMBER NOT NULL
, legacy_answer VARCHAR2(4000) NOT NULL
, CONSTRAINT unmapped_project_data_fk FOREIGN KEY (legacy_question_id) REFERENCES pathfinder_migration.unmapped_project_questions (id)
) TABLESPACE tbsdata;

CREATE TABLE pathfinder_migration.unmapped_project_comments (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_id NUMBER NOT NULL
, comment_text CLOB NOT NULL
, comment_datetime TIMESTAMP NOT NULL
, commented_by_wua_id NUMBER NOT NULL
, comment_type VARCHAR2(4000) NOT NULL
) TABLESPACE tbsdata;

CREATE TABLE pathfinder_migration.unmapped_project_migration_log (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_id NUMBER
, migration_status VARCHAR2(4000)
, system_message CLOB
) TABLESPACE tbsdata;

CREATE TABLE pathfinder_migration.unmapped_detail_migration_log (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, legacy_project_id NUMBER
, legacy_project_detail_id NUMBER
, migration_status VARCHAR2(4000)
, system_message CLOB
) TABLESPACE tbsdata;

INSERT INTO pathfinder_migration.unmapped_project_questions(
  legacy_question_mnem
, legacy_question_text
, legacy_hint_text
)
VALUES(
  'SUMMARY_OF_UPDATE'
, 'Summary of Update as listed below'
, 'Please provide a concise explantion of what you have changed below. What is entered here will not be published.'
);

INSERT INTO pathfinder_migration.unmapped_project_questions(
  legacy_question_mnem
, legacy_question_text
, legacy_hint_text
)
VALUES(
  'ORIGINAL_PRODUCTION_QUARTER'
, 'Original Production Quarter'
, 'In the case of a re-development, please enter the date that production began originally here.'
);

INSERT INTO pathfinder_migration.unmapped_project_questions(
  legacy_question_mnem
, legacy_question_text
, legacy_hint_text
)
VALUES(
  'ORIGINAL_PRODUCTION_YEAR'
, 'Original Production Year'
, 'In the case of a re-development, please enter the date that production began originally here.'
);

INSERT INTO pathfinder_migration.unmapped_project_questions(
  legacy_question_mnem
, legacy_question_text
, legacy_hint_text
)
VALUES(
  'UNDER_CONSTRUCTION_FLAG'
, 'Under Construction?'
, 'Tick this box when the major contracts (> £10m) have been awarded and submit the date. Please then list these contracts below in the contracts awarded section.'
);

INSERT INTO pathfinder_migration.unmapped_project_questions(
  legacy_question_mnem
, legacy_question_text
)
VALUES(
  'CONSTRUCTION_DATE'
, 'Under Construction Date'
);

COMMIT;