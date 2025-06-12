UPDATE ${datasource.user}.project_information
SET
    field_stage = 'OIL_AND_GAS'
  , field_stage_sub_category = 'DISCOVERY'
WHERE field_stage = 'DISCOVERY';

UPDATE ${datasource.user}.project_information
SET
    field_stage = 'OIL_AND_GAS'
  , field_stage_sub_category = 'DEVELOPMENT'
WHERE field_stage = 'DEVELOPMENT';

UPDATE ${datasource.user}.project_information
SET
    field_stage = 'OIL_AND_GAS'
  , field_stage_sub_category = 'DECOMMISSIONING'
WHERE field_stage = 'DECOMMISSIONING';

UPDATE ${datasource.user}.subscriber_field_stage_preferences
SET field_stage = 'OIL_AND_GAS'
WHERE field_stage = 'DISCOVERY';

UPDATE ${datasource.user}.subscriber_field_stage_preferences
SET field_stage = 'OIL_AND_GAS'
WHERE field_stage = 'DEVELOPMENT';

UPDATE ${datasource.user}.subscriber_field_stage_preferences
SET field_stage = 'OIL_AND_GAS'
WHERE field_stage = 'DECOMMISSIONING';
