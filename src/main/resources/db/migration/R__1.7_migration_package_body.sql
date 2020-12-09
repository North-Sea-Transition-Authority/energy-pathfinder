CREATE OR REPLACE PACKAGE BODY ${datasource.migration-user}.migration AS

  /**
    Utility procedure to raise a nicely formatted exception message with
    a custom message prefix. Consumers should call this method inside their exception
    handling blocks.
    @param p_message_prefix The text to prefix the exception message with
   */
  PROCEDURE raise_exception_with_trace(
    p_message_prefix IN VARCHAR2
  )
  IS

  BEGIN

    raise_application_error(
      -20999
    , p_message_prefix || CHR (10) || SQLERRM || CHR(10) || dbms_utility.format_error_backtrace()
    );

  END raise_exception_with_trace;

  /**
    @return a populated map of legacy project detail statuses and the status they map to in the new model.
   */
  FUNCTION get_project_detail_status_map
  RETURN project_detail_status_type
  IS

    l_project_status_map project_detail_status_type;

  BEGIN

    l_project_status_map('CURRENT') := K_NEW_PUBLISHED_STATUS;
    l_project_status_map('HISTORICAL') := K_NEW_PUBLISHED_STATUS;
    l_project_status_map('ARCHIVED') := K_NEW_ARCHIVED_STATUS;

    RETURN l_project_status_map;

  END get_project_detail_status_map;

  /**
    Procedure to populate the project_migration_log table. This is being
    done in a procedure so we always populate the log table with all the latest
    projects from the legacy model when the migration is being run.
   */
  PROCEDURE populate_project_migration_log
  IS

  BEGIN

    INSERT INTO ${datasource.migration-user}.project_migration_log(
      legacy_project_id
    , migration_status
    , system_message
    )
    SELECT
      pp.id
    , K_PENDING_MIGRATION_STATUS
    , EMPTY_CLOB()
    FROM decmgr.path_projects pp
    -- Safety check to not populate with duplicate projects
    WHERE pp.id NOT IN (
      SELECT pml.legacy_project_id
      FROM ${datasource.migration-user}.project_migration_log pml
    );

  END populate_project_migration_log;

  /**
    Procedure to populate the project_detail_migration_log table. This is being
    done in a procedure so we always populate the log table with all the latest
    projects from the legacy model when the migration is being run.
   */
  PROCEDURE populate_detail_migration_log
  IS

  BEGIN

    INSERT INTO ${datasource.migration-user}.project_detail_migration_log(
      legacy_project_id
    , legacy_project_detail_id
    , migration_status
    , system_message
    )
    SELECT
      ppd.path_project_id
    , ppd.id
    , K_PENDING_MIGRATION_STATUS
    , EMPTY_CLOB()
    FROM decmgr.path_project_details ppd
    -- Safety check to not populate with duplicate projects
    WHERE ppd.id NOT IN (
      SELECT pml.legacy_project_detail_id
      FROM ${datasource.migration-user}.project_detail_migration_log pml
    );

  END populate_detail_migration_log;

  /**
    Procedure to populate the migration control tables before the migration runs.
   */
  PROCEDURE populate_migration_logs
  IS

  BEGIN

    populate_project_migration_log();
    populate_detail_migration_log();

    COMMIT;

  END populate_migration_logs;

  /**
    Utility function to write to the project_migration_log table. The write to the log is autonomous so will
    be persisted regardless of errors in the consumer.
    @param p_legacy_project_id The legacy project id
    @param p_migration_status The status of the record
    @param p_new_project_id The project id p_legacy_project_id has been mapped to in the new model
    @param p_system_message The message to append to the log
   */
  PROCEDURE log_project_migration(
    p_legacy_project_id IN ${datasource.migration-user}.project_migration_log.legacy_project_id%TYPE
  , p_migration_status IN ${datasource.migration-user}.project_migration_log.migration_status%TYPE DEFAULT NULL
  , p_new_project_id IN ${datasource.migration-user}.project_migration_log.new_project_id%TYPE DEFAULT NULL
  , p_system_message IN ${datasource.migration-user}.project_migration_log.system_message%TYPE
  )
  IS

    PRAGMA AUTONOMOUS_TRANSACTION;

  BEGIN

    UPDATE ${datasource.migration-user}.project_migration_log pml
    SET
      pml.migration_status = COALESCE(p_migration_status, pml.migration_status)
    , pml.system_message = pml.system_message || CHR(10) || TO_CHAR(SYSTIMESTAMP) || ': ' || p_system_message || CHR(10)
    , pml.new_project_id = COALESCE(p_new_project_id, pml.new_project_id)
    WHERE pml.legacy_project_id = p_legacy_project_id;

    COMMIT;

  END log_project_migration;

  /**
    Utility function to write to the project_detail_migration_log table. The write to the log is autonomous so will
    be persisted regardless of errors in the consumer.
    @param p_legacy_project_detail_id The legacy project detail id
    @param p_migration_status The status of the record
    @param p_new_project_detail_id The project detail id p_legacy_project_detail_id has been mapped to in the new model
    @param p_system_message The message to append to the log
   */
  PROCEDURE log_project_detail_migration(
    p_legacy_project_detail_id IN ${datasource.migration-user}.project_detail_migration_log.legacy_project_detail_id%TYPE
  , p_migration_status IN ${datasource.migration-user}.project_detail_migration_log.migration_status%TYPE DEFAULT NULL
  , p_new_project_detail_id IN ${datasource.migration-user}.project_detail_migration_log.new_project_detail_id%TYPE DEFAULT NULL
  , p_system_message IN ${datasource.migration-user}.project_detail_migration_log.system_message%TYPE
  )
  IS

    PRAGMA AUTONOMOUS_TRANSACTION;

  BEGIN

    UPDATE ${datasource.migration-user}.project_detail_migration_log pdml
    SET
      pdml.migration_status = COALESCE(p_migration_status, pdml.migration_status)
    , pdml.system_message = pdml.system_message || CHR(10) || TO_CHAR(SYSTIMESTAMP) || ': ' || p_system_message || CHR(10)
    , pdml.new_project_detail_id = COALESCE(p_new_project_detail_id, pdml.new_project_detail_id)
    WHERE pdml.legacy_project_detail_id = p_legacy_project_detail_id;

    COMMIT;

  END log_project_detail_migration;

  /**
    Utility procedure to bulk update the migration_status, new_project_detail_id and system_message for
    all project_detail_migration_log records associated to p_legacy_project_id. The write to the log is autonomous
    so will be persisted regardless of errors in the consumer.
    @param p_legacy_project_id The legacy project id that is being migrated
    @param p_system_message The message to write to all the project detail migration logs for this project
   */
  PROCEDURE reset_project_detail_log(
    p_legacy_project_id IN ${datasource.migration-user}.project_detail_migration_log.legacy_project_id%TYPE
  , p_system_message IN ${datasource.migration-user}.project_detail_migration_log.system_message%TYPE
  )
  IS

    PRAGMA AUTONOMOUS_TRANSACTION;

  BEGIN

    UPDATE ${datasource.migration-user}.project_detail_migration_log pdml
    SET
      pdml.migration_status = K_PENDING_MIGRATION_STATUS
    , pdml.system_message = pdml.system_message || CHR(10) || TO_CHAR(SYSTIMESTAMP) || ': ' || p_system_message || CHR(10)
    , pdml.new_project_detail_id = NULL
    WHERE pdml.legacy_project_id = p_legacy_project_id;

    COMMIT;

  END reset_project_detail_log;

  /**
    Function to create a new record in the projects table in the new service model
    @param p_legacy_project_id The id of the legacy project being migrated
    @return The id of the new project record created for p_legacy_project_id
   */
  FUNCTION create_project_record(
    p_legacy_project_id IN decmgr.path_projects.id%TYPE
  ) RETURN NUMBER
  IS

    l_new_project_id NUMBER;

  BEGIN

    INSERT INTO ${datasource.user}.projects(
      created_datetime
    )
    VALUES(
      SYSTIMESTAMP
    )
    RETURNING id INTO l_new_project_id;

    log_project_migration(
      p_legacy_project_id => p_legacy_project_id
    , p_new_project_id => l_new_project_id
    , p_system_message => 'Created new PROJECTS record with ID ' || l_new_project_id
    );

    RETURN l_new_project_id;

  EXCEPTION WHEN OTHERS THEN

    raise_exception_with_trace(
      p_message_prefix => 'ERROR in create_project_record(p_legacy_project_id => ' || p_legacy_project_id || ')'
    );

  END create_project_record;

  /**
    Procedure to create a single project_details record in the new service model.
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_new_project_id The id of the parent project the new detail record should be associated to
    @param p_status The legacy status of the project detail record
    @param p_version The version number of the project detail record
    @param p_is_current_version A integer flag to signify if the project detail record is the current version or not
    @param p_created_by_wua The WUA id of the user who created the legacy project detail record
    @param p_submitted_datetime The timestamp of when the legacy project detail record was created
    @param po_new_project_detail_id The id of the newly create project detail record
    @param po_new_project_detail_status The status of the newly created project detail record
   */
  PROCEDURE create_project_detail_record(
    p_legacy_project_detail_id decmgr.path_project_details.id%TYPE
  , p_new_project_id ${datasource.user}.project_details.project_id%TYPE
  , p_status IN ${datasource.user}.project_details.status%TYPE
  , p_version IN ${datasource.user}.project_details.version%TYPE
  , p_is_current_version IN ${datasource.user}.project_details.is_current_version%TYPE
  , p_created_by_wua IN ${datasource.user}.project_details.created_by_wua%TYPE
  , p_submitted_datetime IN ${datasource.user}.project_details.submitted_datetime%TYPE
  , po_new_project_detail_id OUT ${datasource.user}.project_details.id%TYPE
  , po_new_project_detail_status OUT ${datasource.user}.project_details.status%TYPE
  )
  IS

    l_project_detail_status_map project_detail_status_type := get_project_detail_status_map();

  BEGIN

    INSERT INTO ${datasource.user}.project_details(
       project_id
     , status
     , version
     , is_current_version
     , created_by_wua
     , submitted_datetime
     , submitted_by_wua
     , is_migrated
    )
    VALUES(
      p_new_project_id
    , l_project_detail_status_map(p_status)
    , p_version
    , p_is_current_version
    , p_created_by_wua
    , p_submitted_datetime
    , p_created_by_wua
    , 1
    )
    RETURNING
      id
    , status
    INTO
      po_new_project_detail_id
    , po_new_project_detail_status;

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_new_project_detail_id => po_new_project_detail_id
    , p_system_message => 'Created new PROJECT_DETAILS record with ID ' || po_new_project_detail_id
    );

  EXCEPTION WHEN OTHERS THEN

    raise_exception_with_trace(
      p_message_prefix => 'ERROR in create_project_detail_record(' || CHR(10)
        || '  p_legacy_project_detail_id => ' || p_legacy_project_detail_id || CHR(10)
        || ', p_new_project_id => ' || p_new_project_id || CHR(10)
        || ', p_status => ' || p_status || CHR(10)
        || ', p_version => ' || p_version || CHR(10)
        || ', p_is_current_version => ' || p_is_current_version || CHR(10)
        || ', p_created_by_wua => ' || p_created_by_wua || CHR(10)
        || ', p_submitted_datetime => ' || p_submitted_datetime || CHR(10)
        || ')'
    );

  END create_project_detail_record;

  /**
    Procedure to create a record in the project_publishing_details table in the new service model.
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_new_project_detail_id The detail id the publish record should be associated to
    @param p_published_datetime The timestamp of when the legacy project detail record was published
    @param p_published_by_wua The WUA id who published the legacy project detail record
   */
  PROCEDURE create_project_publish_record(
    p_legacy_project_detail_id IN decmgr.path_projects.id%TYPE
  , p_new_project_detail_id IN ${datasource.user}.project_publishing_details.project_detail_id%TYPE
  , p_published_datetime IN ${datasource.user}.project_publishing_details.published_datetime%TYPE
  , p_published_by_wua IN ${datasource.user}.project_publishing_details.publisher_wua_id%TYPE
  )
  IS

    l_new_publish_detail_id ${datasource.user}.project_publishing_details.id%TYPE;

  BEGIN

    INSERT INTO ${datasource.user}.project_publishing_details(
      project_detail_id
    , published_datetime
    , publisher_wua_id
    )
    VALUES(
      p_new_project_detail_id
    , p_published_datetime
    , p_published_by_wua
    )
    RETURNING id INTO l_new_publish_detail_id;

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message =>
        'Created PROJECT_PUBLISHING_DETAILS record with ID '
        || l_new_publish_detail_id || ' for project detail ID ' || p_new_project_detail_id
    );

  EXCEPTION WHEN OTHERS THEN

    raise_exception_with_trace(
      p_message_prefix => 'ERROR in create_project_publish_record(' || CHR(10)
        || '  p_legacy_project_detail_id => ' || p_legacy_project_detail_id || CHR(10)
        || ', p_new_project_detail_id => ' || p_new_project_detail_id || CHR(10)
        || ', p_published_datetime => ' || p_published_datetime || CHR(10)
        || ', p_published_by_wua => ' || p_published_by_wua || CHR(10)
        || ')'
    );

  END create_project_publish_record;

  /**
    Procedure to create all of the detail records and associated form data for a given legacy project.
    @param p_legacy_project_id The id of the legacy project
    @parma p_new_project_id The id of the project p_legacy_project_id has been mapped to in the new service model
   */
  PROCEDURE create_project_detail_records(
    p_legacy_project_id IN decmgr.path_projects.id%TYPE
  , p_new_project_id IN ${datasource.user}.projects.id%TYPE
  )
  IS

    l_legacy_project_detail_id decmgr.path_project_details.id%TYPE;
    l_new_project_detail_id ${datasource.user}.project_details.id%TYPE;

    l_new_detail_status ${datasource.user}.project_details.status%TYPE;
    l_new_detail_submitted_date ${datasource.user}.project_details.submitted_datetime%TYPE;
    l_new_detail_submitted_by_wua ${datasource.user}.project_details.submitted_by_wua%TYPE;

  BEGIN

    FOR project_detail IN (
      SELECT
        ppd.id legacy_project_detail_id
      , ppd.status
      , ROW_NUMBER() OVER(PARTITION BY pp.id ORDER BY ppd.start_datetime) version
      , DECODE(ppd.status_control, 'C', 1, 0) is_current_version
      , ppd.created_by_wua_id created_by_wua
      , ppd.start_datetime submitted_datetime
      FROM decmgr.path_projects pp
      JOIN decmgr.path_project_details ppd ON ppd.path_project_id = pp.id
      JOIN ${datasource.migration-user}.project_detail_migration_log pdml ON pdml.legacy_project_detail_id = ppd.id
      WHERE pp.id = p_legacy_project_id
      AND pdml.migration_status = K_PENDING_MIGRATION_STATUS
      ORDER BY ppd.start_datetime
    )
    LOOP

      l_legacy_project_detail_id := project_detail.legacy_project_detail_id;
      l_new_detail_submitted_date := project_detail.submitted_datetime;
      l_new_detail_submitted_by_wua := project_detail.created_by_wua;

      log_project_detail_migration(
        p_legacy_project_detail_id => l_legacy_project_detail_id
      , p_migration_status => K_PROCESSING_MIGRATION_STATUS
      , p_new_project_detail_id => l_new_project_detail_id
      , p_system_message => 'Starting migration for legacy project detail with ID ' || l_legacy_project_detail_id
      );

      create_project_detail_record(
        p_legacy_project_detail_id => l_legacy_project_detail_id
      , p_new_project_id => p_new_project_id
      , p_status => project_detail.status
      , p_version => project_detail.version
      , p_is_current_version => project_detail.is_current_version
      , p_created_by_wua => l_new_detail_submitted_by_wua
      , p_submitted_datetime => l_new_detail_submitted_date
      , po_new_project_detail_id => l_new_project_detail_id
      , po_new_project_detail_status => l_new_detail_status
      );

      IF l_new_detail_status = K_NEW_PUBLISHED_STATUS THEN

        create_project_publish_record(
          p_legacy_project_detail_id => l_legacy_project_detail_id
        , p_new_project_detail_id => l_new_project_detail_id
        , p_published_datetime => l_new_detail_submitted_date
        , p_published_by_wua => l_new_detail_submitted_by_wua
        );

      END IF;

      log_project_detail_migration(
        p_legacy_project_detail_id => l_legacy_project_detail_id
      , p_migration_status => K_COMPLETE_MIGRATION_STATUS
      , p_new_project_detail_id => l_new_project_detail_id
      , p_system_message => 'Completed migration of project detail record and associated project data'
      );

    END LOOP;

  END create_project_detail_records;

  PROCEDURE migrate_project(
    p_legacy_project_id IN NUMBER
  )
  IS

    l_new_project_id NUMBER;

  BEGIN

    SAVEPOINT sp_before_migrate_project;

    BEGIN

      log_project_migration(
        p_legacy_project_id => p_legacy_project_id
      , p_migration_status => K_PROCESSING_MIGRATION_STATUS
      , p_system_message => 'Starting migration for legacy project with ID ' || p_legacy_project_id
      );

      l_new_project_id := create_project_record(
        p_legacy_project_id => p_legacy_project_id
      );

      log_project_migration(
        p_legacy_project_id => p_legacy_project_id
      , p_migration_status => K_PROCESSING_MIGRATION_STATUS
      , p_system_message => 'Starting migration of project detail records for legacy project with ID ' || p_legacy_project_id
      );

      create_project_detail_records(
        p_legacy_project_id => p_legacy_project_id
      , p_new_project_id => l_new_project_id
      );

      log_project_migration(
        p_legacy_project_id => p_legacy_project_id
      , p_migration_status => K_COMPLETE_MIGRATION_STATUS
      , p_system_message => 'Completed migration for legacy project with ID ' || p_legacy_project_id
      );

      COMMIT;

    EXCEPTION WHEN OTHERS THEN

      ROLLBACK TO SAVEPOINT sp_before_migrate_project;

      log_project_migration(
        p_legacy_project_id => p_legacy_project_id
      , p_migration_status => K_ERROR_MIGRATION_STATUS
      , p_system_message =>
          'Error migrating legacy project with ID ' || p_legacy_project_id || '. '
          || 'Migration for project has been rolled back.' || CHR(10)
          || 'Failed with error:' || CHR(10) || SQLERRM || CHR(10) || dbms_utility.format_error_backtrace()
      );

      reset_project_detail_log(
        p_legacy_project_id => p_legacy_project_id
      , p_system_message => 'Error migrating this project. Migration has been rolled back for this project. See project log for trace'
      );

    END;

  END migrate_project;

  PROCEDURE migrate_projects
  IS

  BEGIN

    populate_migration_logs();

    FOR project IN (
      SELECT pml.legacy_project_id id
      FROM ${datasource.migration-user}.project_migration_log pml
      WHERE pml.migration_status = K_PENDING_MIGRATION_STATUS
      ORDER BY pml.legacy_project_id
    )
    LOOP

      migrate_project(
        p_legacy_project_id => project.id
      );

    END LOOP;

  END migrate_projects;

END migration;