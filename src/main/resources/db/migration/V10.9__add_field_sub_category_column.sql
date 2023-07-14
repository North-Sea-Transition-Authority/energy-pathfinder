ALTER TABLE ${datasource.user}.project_information
ADD field_stage_sub_category VARCHAR2(4000);

--Migrate the Energy Transition values
UPDATE ${datasource.user}.project_information
SET field_stage = 'OFFSHORE_WIND'
, field_stage_sub_category = 'FIXED_BOTTOM_OFFSHORE_WIND'
WHERE field_stage = 'ENERGY_TRANSITION'
AND energy_transition_category = 'OFFSHORE_POWER_GENERATION';

UPDATE ${datasource.user}.project_information
SET field_stage = 'CARBON_CAPTURE_AND_STORAGE'
, field_stage_sub_category = 'TRANSPORTATION_AND_STORAGE'
WHERE field_stage = 'ENERGY_TRANSITION'
AND energy_transition_category = 'CARBON_CAPTURE_UTILISATION_AND_STORAGE';

UPDATE ${datasource.user}.project_information
SET field_stage = 'HYDROGEN'
WHERE field_stage = 'ENERGY_TRANSITION'
AND energy_transition_category = 'HYDROGEN';

UPDATE ${datasource.user}.project_information
SET field_stage = 'OFFSHORE_ELECTRIFICATION'
WHERE field_stage = 'ENERGY_TRANSITION'
AND energy_transition_category = 'ELECTRIFICATION';

UPDATE ${datasource.user}.project_information
SET field_stage = NULL
WHERE field_stage = 'ENERGY_TRANSITION'
AND energy_transition_category IS NULL;
