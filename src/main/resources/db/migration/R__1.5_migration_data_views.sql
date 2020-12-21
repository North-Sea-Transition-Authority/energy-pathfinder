CREATE OR REPLACE VIEW ${datasource.migration-user}.legacy_project_data AS (
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

CREATE OR REPLACE VIEW ${datasource.migration-user}.legacy_project_challenges AS (
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

CREATE OR REPLACE VIEW ${datasource.migration-user}.legacy_project_contracts AS (
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