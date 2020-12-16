ALTER TABLE ${datasource.user}.project_details ADD (
  created_datetime TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);