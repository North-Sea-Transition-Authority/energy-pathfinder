ALTER TABLE ${datasource.user}.work_plan_collaborations
ADD added_by_organisation_group NUMBER;

--Set all work plan collaboration opportunities as being added by the current project operator.
--Prior to project contributors only the operator could access the task list sections
UPDATE ${datasource.user}.work_plan_collaborations wpc
SET wpc.added_by_organisation_group = (
  SELECT po.operator_org_grp_id
  FROM ${datasource.user}.project_operators po
  WHERE po.project_detail_id = wpc.project_detail_id
);

ALTER TABLE ${datasource.user}.work_plan_collaborations
MODIFY added_by_organisation_group NOT NULL;