ALTER TABLE ${datasource.user}.work_plan_upcoming_tenders
ADD(
  contract_term_duration NUMBER
, contract_term_duration_period VARCHAR2(4000)
);