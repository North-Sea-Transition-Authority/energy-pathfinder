CREATE OR REPLACE VIEW ${datasource.user}.wellbores AS
SELECT
  aew.wellbore_id id
, aew.well_registration_no registration_no
FROM wellmgr.api_extant_wellbores aew
WHERE aew.mechanical_status != 'PLANNED';
