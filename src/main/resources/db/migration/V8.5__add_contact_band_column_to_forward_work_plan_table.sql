ALTER TABLE ${datasource.user}.work_plan_upcoming_tenders
ADD(contract_band VARCHAR2(4000));

UPDATE ${datasource.user}.work_plan_upcoming_tenders wput
SET wput.contract_band = 'LESS_THAN_5M';

ALTER TABLE ${datasource.user}.work_plan_upcoming_tenders
MODIFY(contract_band VARCHAR2(4000) NOT NULL);