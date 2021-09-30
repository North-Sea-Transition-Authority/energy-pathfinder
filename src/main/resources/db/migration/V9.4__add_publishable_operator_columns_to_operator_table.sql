ALTER TABLE ${datasource.user}.project_operators
ADD (
  publish_as_project_operator NUMBER,
  publishable_org_unit_id NUMBER
);