ALTER TABLE ${datasource.user}.project_transfers
ADD (
  publish_as_project_operator NUMBER,
  publishable_org_unit_id NUMBER
);