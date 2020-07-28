DECLARE

  TYPE role_migration_type IS TABLE OF bpmmgr.varchar2_list_type
  INDEX BY VARCHAR2(4000);

  K_LEGACY_REGULATOR_TEAM CONSTANT VARCHAR2(4000) := 'PATH_ADMIN_TEAM';
  K_NEW_REGULATOR_TEAM CONSTANT VARCHAR2(4000) := 'PATHFINDER_REGULATOR_TEAM';
  K_LEGACY_ORGANISATION_TEAM CONSTANT VARCHAR2(4000) := 'PATH_OPERATOR_TEAM';
  K_NEW_ORGANISATION_TEAM CONSTANT VARCHAR2(4000) := 'PATHFINDER_ORGANISATION_TEAM';

  l_legacy_regulator_resource_id NUMBER;
  l_new_regulator_resource_id NUMBER;
  l_new_org_grp_resource_id NUMBER;

  FUNCTION create_regulator_role_map
  RETURN role_migration_type
  IS

    l_regulator_role_map role_migration_type;

  BEGIN

    l_regulator_role_map('RESOURCE_COORDINATOR') := bpmmgr.varchar2_list_type('RESOURCE_COORDINATOR', 'ORGANISATION_MANAGER');
    l_regulator_role_map('PATH_COMMENTER') := bpmmgr.varchar2_list_type('COMMENT_PROVIDER');
    l_regulator_role_map('PATH_VIEWER') := bpmmgr.varchar2_list_type('PROJECT_VIEWER');

    RETURN l_regulator_role_map;

  END create_regulator_role_map;

  FUNCTION create_organisation_role_map
  RETURN role_migration_type
  IS

    l_organisation_role_map role_migration_type;

  BEGIN

    l_organisation_role_map('SYSTEM_USER') := bpmmgr.varchar2_list_type('RESOURCE_COORDINATOR', 'PROJECT_SUBMITTER');

    RETURN l_organisation_role_map;

  END create_organisation_role_map;

  PROCEDURE migrate_team(
    p_legacy_resource_id IN NUMBER
  , p_new_resource_id IN NUMBER
  , p_role_migration_type IN role_migration_type
  )
  IS

    l_new_team_roles bpmmgr.varchar2_list_type := bpmmgr.varchar2_list_type();
    l_mapped_role_list bpmmgr.varchar2_list_type := bpmmgr.varchar2_list_type();

  BEGIN

    FOR person IN (
      SELECT
        rmc.person_id id
      , stagg(rmc.role_name) legacy_roles
      FROM decmgr.resource_members_current rmc
      JOIN securemgr.web_user_accounts wua ON wua.id = rmc.wua_id
      WHERE rmc.res_id = p_legacy_resource_id
      AND wua.account_status = 'ACTIVE'
      AND NOT EXISTS (
        -- Not already migrated
        SELECT DISTINCT new_resource.person_id
        FROM decmgr.resource_members_current new_resource
        WHERE new_resource.res_id = p_new_resource_id
        AND new_resource.person_id = rmc.person_id
      )
      GROUP BY rmc.person_id
    )
    LOOP

      l_new_team_roles := bpmmgr.varchar2_list_type();

      FOR legacy_role IN (
        SELECT t.column_value name
        FROM TABLE(person.legacy_roles) t
      )
      LOOP

        l_mapped_role_list := p_role_migration_type(legacy_role.name);

        FOR idx IN l_mapped_role_list.FIRST..l_mapped_role_list.LAST LOOP

          l_new_team_roles.EXTEND;
          l_new_team_roles(l_new_team_roles.LAST) := l_mapped_role_list(idx);

        END LOOP;

      END LOOP;

      decmgr.contact.add_members_to_roles(
        p_res_id => p_new_resource_id
      , p_role_name_list => l_new_team_roles
      , p_person_id_list => bpmmgr.number_list_type(person.id)
      , p_requesting_wua_id => 1
      );

    END LOOP;

  END migrate_team;

BEGIN

  SELECT r.id
  INTO l_new_regulator_resource_id
  FROM decmgr.resources r
  WHERE r.res_type = K_NEW_REGULATOR_TEAM;

  SELECT r.id
  INTO l_legacy_regulator_resource_id
  FROM decmgr.resources r
  WHERE r.res_type = K_LEGACY_REGULATOR_TEAM;

  migrate_team(
    p_legacy_resource_id => l_legacy_regulator_resource_id
  , p_new_resource_id => l_new_regulator_resource_id
  , p_role_migration_type => create_regulator_role_map()
  );

  FOR legacy_org_grp_resource IN (
    WITH pathfinder_teams AS (
      SELECT
        ruc.res_id id
      , xr.res_type type
      , ruc.uref
      FROM decmgr.resource_usages_current ruc
      JOIN decmgr.current_organisation_groups cog ON cog.id || '++REGORGGRP' = ruc.uref
      JOIN decmgr.xview_resources xr ON xr.res_id = ruc.res_id
      WHERE cog.org_grp_type = 'REG'
      AND xr.res_type IN(K_LEGACY_ORGANISATION_TEAM, K_NEW_ORGANISATION_TEAM)
    )
    SELECT pt.*
    FROM pathfinder_teams pt
    WHERE pt.type = K_LEGACY_ORGANISATION_TEAM
    -- Only migrate if we have made a new pathfinder org team
    AND EXISTS (
      SELECT 1
      FROM pathfinder_teams new_team
      WHERE new_team.type = K_NEW_ORGANISATION_TEAM
      AND new_team.uref = pt.uref
    )
  )
  LOOP

    SELECT r.id
    INTO l_new_org_grp_resource_id
    FROM decmgr.resources r
    JOIN decmgr.resource_usages_current ruc ON ruc.res_id = r.id
    WHERE r.res_type = K_NEW_ORGANISATION_TEAM
    AND ruc.uref = legacy_org_grp_resource.uref;

    migrate_team(
      p_legacy_resource_id => legacy_org_grp_resource.id
    , p_new_resource_id => l_new_org_grp_resource_id
    , p_role_migration_type => create_organisation_role_map()
    );

  END LOOP;

  COMMIT;

END;