CREATE OR REPLACE VIEW ${datasource.user}.wellbores AS
SELECT
  aew.wellbore_id id
, aew.well_registration_no registration_no
, aew.quadrant_no
, aew.block_no
, aew.block_suffix
, aew.platform_letter
, aew.drilling_seq_no
, aew.well_suffix
FROM wellmgr.api_extant_wellbores aew
WHERE aew.mechanical_status != 'PLANNED';
