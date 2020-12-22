ALTER TABLE ${datasource.user}.no_update_notifications
ADD (
  supply_chain_reason CLOB
, regulator_reason CLOB
);

UPDATE ${datasource.user}.no_update_notifications
SET
  supply_chain_reason = reason_no_update_required
, regulator_reason = reason_no_update_required;

ALTER TABLE ${datasource.user}.no_update_notifications
MODIFY supply_chain_reason NOT NULL;

ALTER TABLE ${datasource.user}.no_update_notifications
DROP COLUMN reason_no_update_required;
