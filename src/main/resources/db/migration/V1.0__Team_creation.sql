DECLARE

  K_NEW_REGULATOR_TEAM_TYPE CONSTANT VARCHAR2(4000) := 'PATHFINDER_REGULATOR_TEAM';
  K_NEW_REGULATOR_TEAM_NAME CONSTANT VARCHAR2(4000) := 'Pathfinder regulator team';
  K_OLD_ORGANISATION_TEAM_TYPE CONSTANT VARCHAR2(4000) := 'PATH_OPERATOR_TEAM';
  K_NEW_ORGANISATION_TEAM_TYPE CONSTANT VARCHAR2(4000) := 'PATHFINDER_ORGANISATION_TEAM';
  K_NEW_ORGANISATION_TEAM_NAME CONSTANT VARCHAR2(4000) := 'Pathfinder organisation team';

  l_organisation_resource_id NUMBER;
  l_regulator_resource_id NUMBER;

BEGIN

  BEGIN

    SELECT r.id
    INTO l_regulator_resource_id
    FROM decmgr.resources r
    WHERE r.res_type = K_NEW_REGULATOR_TEAM_TYPE;

  EXCEPTION WHEN NO_DATA_FOUND THEN

    l_regulator_resource_id := NULL;

  END;

  IF l_regulator_resource_id IS NULL THEN

    l_regulator_resource_id := decmgr.contact.create_default_team(
      p_resource_type => K_NEW_REGULATOR_TEAM_TYPE
    , p_resource_name => K_NEW_REGULATOR_TEAM_NAME
    , p_resource_desc => K_NEW_REGULATOR_TEAM_NAME
    , p_creating_wua_id => 1
    );

  END IF;

  FOR org_group IN (
    SELECT
      cog.id || '++REGORGGRP' uref
    , cog.name org_name
    FROM decmgr.current_organisation_groups cog
    WHERE cog.org_grp_type = 'REG'
    AND EXISTS (
      SELECT 1
      FROM decmgr.current_org_grp_organisations cogo
      WHERE cogo.org_grp_id = cog.id
    )
    -- Only create if a team doesn't already exist
    AND NOT EXISTS (
      SELECT 1
      FROM decmgr.resource_usages_current ruc
      JOIN decmgr.xview_resources xr ON xr.res_id = ruc.res_id
      WHERE xr.res_type = K_NEW_ORGANISATION_TEAM_TYPE
      AND ruc.uref = cog.id || '++REGORGGRP'
    )
    -- Only create if the organisation had a legacy pathfinder team
    AND EXISTS(
      SELECT 1
      FROM decmgr.resource_usages_current ruc
      JOIN decmgr.xview_resources xr ON xr.res_id = ruc.res_id
      WHERE xr.res_type = K_OLD_ORGANISATION_TEAM_TYPE
      AND ruc.uref = cog.id || '++REGORGGRP'
    )
  )
  LOOP

    l_organisation_resource_id := decmgr.contact.create_default_team(
      p_resource_type => K_NEW_ORGANISATION_TEAM_TYPE
    , p_resource_name => K_NEW_ORGANISATION_TEAM_NAME
    , p_resource_desc => K_NEW_ORGANISATION_TEAM_NAME || ' - ' || org_group.org_name
    , p_uref => org_group.uref
    , p_uref_purpose => 'PRIMARY_DATA'
    , p_creating_wua_id => 1
    );

  END LOOP;

  COMMIT;

END;