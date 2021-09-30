CREATE OR REPLACE VIEW ${datasource.user}.portal_organisation_units AS
SELECT
  xou.organ_id ou_id
, xou.name
, cog.id org_grp_id
, CASE
    WHEN DECODE(xou.is_duplicate, 'Y', 1, 0) = 0 AND xou.end_date IS NULL
      THEN 1
    ELSE 0
  END active
FROM decmgr.xview_organisation_units xou
LEFT JOIN decmgr.current_org_grp_organisations cogo ON cogo.organ_id = xou.organ_id
LEFT JOIN decmgr.current_organisation_groups cog ON cog.id = cogo.org_grp_id AND cog.org_grp_type = 'REG';

CREATE OR REPLACE VIEW ${datasource.user}.portal_organisation_groups AS
SELECT
  cog.id org_grp_id
, cog.name
, cog.short_name
, cog.id || '++' || cog.org_grp_type || 'ORGGRP' uref_value
FROM decmgr.current_organisation_groups cog
WHERE cog.org_grp_type = 'REG';

-- ou_id and org_unit_id are identical in order to reference the PortalOrganisationUnit entity.
CREATE OR REPLACE VIEW ${datasource.user}.portal_org_unit_detail AS
SELECT
  xou.organ_id ou_id
, xou.organ_id org_unit_id
, oad.legal_address
, oad.registered_number
FROM decmgr.xview_organisation_units xou
LEFT JOIN decmgr.organisation_address_details oad ON oad.organ_id = xou.organ_id
WHERE xou.end_date IS NULL
AND xou.is_duplicate IS NULL;
