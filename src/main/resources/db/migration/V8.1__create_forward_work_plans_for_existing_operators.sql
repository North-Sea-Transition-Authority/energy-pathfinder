DECLARE

  K_ORGANISATION_TEAM_TYPE CONSTANT decmgr.resources.res_type%TYPE := 'PATHFINDER_ORGANISATION_TEAM';
  K_FORWARD_WORK_PLAN_TYPE CONSTANT ${datasource.user}.project_details.project_type%TYPE := 'FORWARD_WORK_PLAN';
  K_DRAFT_PROJECT_STATUS CONSTANT ${datasource.user}.project_details.status%TYPE := 'DRAFT';

  l_project_id ${datasource.user}.projects.id%TYPE;
  l_project_detail_id ${datasource.user}.project_details.id%TYPE;

BEGIN

  FOR operator IN (
    SELECT cog.id
    FROM decmgr.resources r
    JOIN decmgr.resource_usages_current ruc ON ruc.res_id = r.id
    JOIN decmgr.current_organisation_groups cog ON cog.id || '++REGORGGRP' = ruc.uref
    WHERE r.res_type = K_ORGANISATION_TEAM_TYPE
    -- the operator doesn't already have a forward work plan project
    AND cog.id NOT IN (
      SELECT po.operator_org_grp_id
      FROM ${datasource.user}.project_details pd
      JOIN ${datasource.user}.project_operators po ON po.project_detail_id = pd.id
      WHERE pd.project_type = K_FORWARD_WORK_PLAN_TYPE
    )
  )
  LOOP

    INSERT INTO ${datasource.user}.projects(
      created_datetime
    )
    VALUES (
      SYSTIMESTAMP
    )
    RETURNING id INTO l_project_id;

    INSERT INTO ${datasource.user}.project_details(
      project_id
    , status
    , version
    , is_current_version
    , created_by_wua
    , is_migrated
    , created_datetime
    , project_type
    )
    VALUES (
      l_project_id
    , K_DRAFT_PROJECT_STATUS
    , 1 -- version
    , 1 -- is_current_version
    , 1 -- system web user account
    , 0 -- is not migration
    , SYSTIMESTAMP
    , K_FORWARD_WORK_PLAN_TYPE
    )
    RETURNING id INTO l_project_detail_id;

    INSERT INTO ${datasource.user}.project_operators(
      project_detail_id
    , operator_org_grp_id
    )
    VALUES(
      l_project_detail_id
    , operator.id
    );

  END LOOP;

  COMMIT;

END;