UPDATE ${datasource.user}.subscriber_field_stage_preferences
SET field_stage = 'ELECTRIFICATION'
WHERE field_stage = 'OFFSHORE_ELECTRIFICATION';

UPDATE ${datasource.user}.subscriber_field_stage_preferences
SET field_stage = 'WIND_ENERGY'
WHERE field_stage = 'OFFSHORE_WIND';
