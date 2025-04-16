UPDATE ${datasource.user}.project_information
SET
  field_stage = 'ELECTRIFICATION'
, field_stage_sub_category = 'OFFSHORE_ELECTRIFICATION'
WHERE field_stage = 'OFFSHORE_ELECTRIFICATION';

UPDATE ${datasource.user}.project_information
SET field_stage = 'WIND_ENERGY'
WHERE field_stage = 'OFFSHORE_WIND';
