INSERT INTO ${datasource.migration-user}.unmapped_project_questions(
  legacy_question_mnem
, legacy_question_text
, legacy_hint_text
)
VALUES(
  'SUMMARY_OF_UPDATE'
, 'Summary of Update as listed below'
, 'Please provide a concise explantion of what you have changed below. What is entered here will not be published.'
);

INSERT INTO ${datasource.migration-user}.unmapped_project_questions(
  legacy_question_mnem
, legacy_question_text
, legacy_hint_text
)
VALUES(
  'ORIGINAL_PRODUCTION_QUARTER'
, 'Original Production Quarter'
, 'In the case of a re-development, please enter the date that production began originally here.'
);

INSERT INTO ${datasource.migration-user}.unmapped_project_questions(
  legacy_question_mnem
, legacy_question_text
, legacy_hint_text
)
VALUES(
  'ORIGINAL_PRODUCTION_YEAR'
, 'Original Production Year'
, 'In the case of a re-development, please enter the date that production began originally here.'
);

INSERT INTO ${datasource.migration-user}.unmapped_project_questions(
  legacy_question_mnem
, legacy_question_text
, legacy_hint_text
)
VALUES(
  'UNDER_CONSTRUCTION_FLAG'
, 'Under Construction?'
, 'Tick this box when the major contracts (> £10m) have been awarded and submit the date. Please then list these contracts below in the contracts awarded section.'
);

INSERT INTO ${datasource.migration-user}.unmapped_project_questions(
  legacy_question_mnem
, legacy_question_text
)
VALUES(
  'CONSTRUCTION_DATE'
, 'Under Construction Date'
);

COMMIT;