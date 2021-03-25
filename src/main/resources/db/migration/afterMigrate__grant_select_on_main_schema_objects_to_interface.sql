/**
  The regulator has a public interface which queries the pathfinder tables in order to
  display data. To avoid potential issues with the source schema the service uses being
  locked by a misconfiguration on the regulator interface we provide a separate schema user
  for them to connect with. In order to ensure the interface schema can see all the relevant objects,
  we loop over all table and views in the source schema and grant SELECT to the interface user after every migration run.
 */
DECLARE

  K_SOURCE_SCHEMA_USER CONSTANT VARCHAR2(30) := UPPER('${datasource.user}');
  K_INTERFACE_SCHEMA_USER CONSTANT VARCHAR2(30) := UPPER('${datasource.public-interface-user}');

BEGIN

  FOR object IN (
    SELECT
      ao.owner
    , ao.object_name
    FROM all_objects ao
    WHERE UPPER(ao.owner) = K_SOURCE_SCHEMA_USER
    AND UPPER(ao.object_type) IN('TABLE', 'VIEW')
  )
  LOOP

    EXECUTE IMMEDIATE 'GRANT SELECT ON ' || object.owner || '.' || object.object_name || ' TO ' || K_INTERFACE_SCHEMA_USER;

  END LOOP;

END;