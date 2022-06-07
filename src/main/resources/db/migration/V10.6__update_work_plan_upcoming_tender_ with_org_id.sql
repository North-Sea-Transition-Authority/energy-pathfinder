ALTER TABLE ${datasource.user}.work_plan_upcoming_tenders
ADD added_by_organisation_group NUMBER;

--Set all forward work plan upcoming tenders as being added by the current project operator.
-- Prior to project contributors only the operator could access the task list sections
UPDATE ${datasource.user}.work_plan_upcoming_tenders up
SET up.added_by_organisation_group = (
  SELECT po.operator_org_grp_id
  FROM ${datasource.user}.project_operators po
  WHERE po.project_detail_id = up.project_detail_id
);

ALTER TABLE ${datasource.user}.work_plan_upcoming_tenders
MODIFY added_by_organisation_group NOT NULL;