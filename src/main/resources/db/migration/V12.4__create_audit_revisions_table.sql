CREATE TABLE ${datasource.user}.audit_revisions (
  rev INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, created_date DATE
);