DECLARE

  K_OLD_SERVICE_NAME_PREFIX CONSTANT VARCHAR2(4000) := 'Pathfinder';
  K_NEW_SERVICE_NAME_PREFIX CONSTANT VARCHAR2(4000) := 'Energy Pathfinder';

BEGIN

  FOR team IN (
    SELECT
      r.id
    , REPLACE(xt.name, K_OLD_SERVICE_NAME_PREFIX, K_NEW_SERVICE_NAME_PREFIX) name
    , REPLACE(xt.description, K_OLD_SERVICE_NAME_PREFIX, K_NEW_SERVICE_NAME_PREFIX) description
    FROM decmgr.resources r
    , XMLTABLE('RESOURCES'
      PASSING r.xml_data
      COLUMNS
        name VARCHAR2(4000) PATH 'RES_NAME/text()'
      , description VARCHAR2(4000) PATH 'DESCRIPTION/text()'
      ) xt
    WHERE r.res_type IN ('PATHFINDER_REGULATOR_TEAM', 'PATHFINDER_ORGANISATION_TEAM')
    )
    LOOP

      decmgr.contact.update_team_name_description(
        p_res_id => team.id
      , p_resource_name => team.name
      , p_resource_desc => team.description
      );

    END LOOP;

  COMMIT;

END;