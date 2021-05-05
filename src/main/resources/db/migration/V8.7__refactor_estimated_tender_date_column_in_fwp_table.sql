ALTER TABLE ${datasource.user}.work_plan_upcoming_tenders DROP (estimated_tender_date);

ALTER TABLE ${datasource.user}.work_plan_upcoming_tenders ADD (
  estimated_tender_date_quarter VARCHAR2(4000),
  estimated_tender_date_year NUMBER
);