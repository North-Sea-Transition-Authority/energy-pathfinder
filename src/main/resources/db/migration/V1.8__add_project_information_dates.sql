ALTER TABLE ${datasource.user}.project_information ADD (
  first_production_date_quarter VARCHAR2(4000)
, first_production_date_year NUMBER
, decom_work_start_date_quarter VARCHAR2(4000)
, decom_work_start_date_year NUMBER
, production_cessation_date TIMESTAMP
);