ALTER TABLE ${datasource.user}.project_locations ADD (
  field_type VARCHAR2(4000),
  water_depth NUMBER,
  approved_fdp NUMBER,
  approved_fdp_date TIMESTAMP,
  approved_decom_program NUMBER,
  approved_decom_program_date TIMESTAMP
);