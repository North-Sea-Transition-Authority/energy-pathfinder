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
, aew.mechanical_status
, aew.operational_status
, aew.subsea_wellhead is_subsea_wellhead
, aew.subarea_operator_ou_id
, aew.competent_operator_ou_id
, aew.responsible_company_ou_id
, aew.licence_master_id
, aew.licence_type
, aew.licence_no
FROM wellmgr.api_extant_wellbores aew
WHERE aew.mechanical_status != 'PLANNED';
