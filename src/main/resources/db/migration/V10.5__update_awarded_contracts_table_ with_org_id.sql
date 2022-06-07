ALTER TABLE ${datasource.user}.awarded_contracts
ADD added_by_organisation_group NUMBER;

--Set all collaboration opportunities as being added by the current project operator.
--Prior to project contributors only the operator could access the task list sections
UPDATE ${datasource.user}.awarded_contracts ac
SET ac.added_by_organisation_group = (
  SELECT po.operator_org_grp_id
  FROM ${datasource.user}.project_operators po
  WHERE po.project_detail_id = ac.project_detail_id
);

ALTER TABLE ${datasource.user}.awarded_contracts
MODIFY added_by_organisation_group NOT NULL;