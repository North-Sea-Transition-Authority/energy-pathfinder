ALTER TABLE ${datasource.user}.project_updates
ADD (
  no_update NUMBER
, reason_no_update_required VARCHAR2(4000)
);

UPDATE ${datasource.user}.project_updates
SET no_update = 0;

ALTER TABLE ${datasource.user}.project_updates
MODIFY no_update NUMBER NOT NULL;
