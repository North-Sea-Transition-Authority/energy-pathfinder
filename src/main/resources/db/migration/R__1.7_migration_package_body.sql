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
    , p_message_prefix || CHR (10) || dbms_utility.format_error_stack() || CHR(10) || dbms_utility.format_error_backtrace()
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
    Procedure to populate the unmapped_project_migration_log table. This is being
    done in a procedure so we always populate the log table with all the latest
    projects from the legacy model when the migration is being run.
   */
  PROCEDURE populate_unmapped_project_log
  IS
  BEGIN

    INSERT INTO ${datasource.migration-user}.unmapped_project_migration_log(
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
      FROM ${datasource.migration-user}.unmapped_project_migration_log pml
    );

  END populate_unmapped_project_log;

  /**
    Procedure to populate the unmapped_detail_migration_log table. This is being
    done in a procedure so we always populate the log table with all the latest
    projects from the legacy model when the migration is being run.
   */
  PROCEDURE populate_unmapped_detail_log
  IS

  BEGIN

    INSERT INTO ${datasource.migration-user}.unmapped_detail_migration_log(
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
      FROM ${datasource.migration-user}.unmapped_detail_migration_log pml
    );

  END populate_unmapped_detail_log;

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
    Procedure to populate the unmapped migration control tables before the migration runs.
   */
  PROCEDURE populate_unmapped_data_logs
  IS

  BEGIN

    populate_unmapped_project_log();
    populate_unmapped_detail_log();

    COMMIT;

  END populate_unmapped_data_logs;

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
    Utility function to write to the unmapped_project_migration_log table. The write to the log is autonomous so will
    be persisted regardless of errors in the consumer.
    @param p_legacy_project_id The legacy project id
    @param p_migration_status The status of the record
    @param p_system_message The message to append to the log
   */
  PROCEDURE log_unmapped_project_migration(
    p_legacy_project_id IN ${datasource.migration-user}.unmapped_project_migration_log.legacy_project_id%TYPE
  , p_migration_status IN ${datasource.migration-user}.unmapped_project_migration_log.migration_status%TYPE DEFAULT NULL
  , p_system_message IN ${datasource.migration-user}.unmapped_project_migration_log.system_message%TYPE
  )
  IS

    PRAGMA AUTONOMOUS_TRANSACTION;

  BEGIN

    UPDATE ${datasource.migration-user}.unmapped_project_migration_log upml
    SET
      upml.migration_status = COALESCE(p_migration_status, upml.migration_status)
    , upml.system_message = upml.system_message || CHR(10) || TO_CHAR(SYSTIMESTAMP) || ': ' || p_system_message || CHR(10)
    WHERE upml.legacy_project_id = p_legacy_project_id;

    COMMIT;

  END log_unmapped_project_migration;

  /**
  Utility function to write to the unmapped_detail_migration_log table. The write to the log is autonomous so will
  be persisted regardless of errors in the consumer.
  @param p_legacy_project_detail_id The legacy project detail id
  @param p_migration_status The status of the record
  @param p_system_message The message to append to the log
 */
  PROCEDURE log_unmapped_detail_migration(
    p_legacy_project_detail_id IN ${datasource.migration-user}.unmapped_detail_migration_log.legacy_project_detail_id%TYPE
  , p_migration_status IN ${datasource.migration-user}.unmapped_detail_migration_log.migration_status%TYPE DEFAULT NULL
  , p_system_message IN ${datasource.migration-user}.unmapped_detail_migration_log.system_message%TYPE
  )
  IS

    PRAGMA AUTONOMOUS_TRANSACTION;

  BEGIN

    UPDATE ${datasource.migration-user}.unmapped_detail_migration_log pudl
    SET
      pudl.migration_status = COALESCE(p_migration_status, pudl.migration_status)
    , pudl.system_message = pudl.system_message || CHR(10) || TO_CHAR(SYSTIMESTAMP) || ': ' || p_system_message || CHR(10)
    WHERE pudl.legacy_project_detail_id = p_legacy_project_detail_id;

    COMMIT;

  END log_unmapped_detail_migration;

  /**
    Utility procedure to bulk update the migration_status and system_message for
    all UNMAPPED_DETAIL_MIGRATION_LOG records associated to p_legacy_project_id. The write to the log is autonomous
    so will be persisted regardless of errors in the consumer.
    @param p_legacy_project_id The legacy project id that is being migrated
    @param p_system_message The message to write to all the project detail migration logs for this project
   */
  PROCEDURE reset_unmapped_detail_log(
    p_legacy_project_id IN ${datasource.migration-user}.unmapped_detail_migration_log.legacy_project_id%TYPE
  , p_system_message IN ${datasource.migration-user}.unmapped_detail_migration_log.system_message%TYPE
  )
  IS

    PRAGMA AUTONOMOUS_TRANSACTION;

  BEGIN

    UPDATE ${datasource.migration-user}.unmapped_detail_migration_log udml
    SET
      udml.migration_status = K_PENDING_MIGRATION_STATUS
    , udml.system_message = udml.system_message || CHR(10) || TO_CHAR(SYSTIMESTAMP) || ': ' || p_system_message || CHR(10)
    WHERE udml.legacy_project_id = p_legacy_project_id;

    COMMIT;

  END reset_unmapped_detail_log;

  /**
    Function to create a new record in the projects table in the new service model
    @param p_legacy_project_id The id of the legacy project being migrated
    @return The id of the new project record created for p_legacy_project_id
   */
  FUNCTION create_project_record(
    p_legacy_project_id IN decmgr.path_projects.id%TYPE
  ) RETURN NUMBER
  IS

    K_DESTINATION_TABLE_NAME CONSTANT VARCHAR2(30) := 'PROJECTS';

    l_created_datetime decmgr.path_project_details.start_datetime%TYPE;
    l_new_project_id ${datasource.user}.projects.id%TYPE;

  BEGIN

    log_project_migration(
      p_legacy_project_id => p_legacy_project_id
    , p_system_message => 'Creating ' || K_DESTINATION_TABLE_NAME || ' record for legacy project with ID ' || p_legacy_project_id
    );

    SELECT MIN(ppd.start_datetime)
    INTO l_created_datetime
    FROM decmgr.path_project_details ppd
    WHERE ppd.path_project_id = p_legacy_project_id;

    INSERT INTO ${datasource.user}.projects(
      created_datetime
    )
    VALUES(
      l_created_datetime
    )
    RETURNING id INTO l_new_project_id;

    log_project_migration(
      p_legacy_project_id => p_legacy_project_id
    , p_new_project_id => l_new_project_id
    , p_system_message => 'Created new ' || K_DESTINATION_TABLE_NAME || ' record with ID ' || l_new_project_id
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

    K_DESTINATION_TABLE_NAME CONSTANT VARCHAR2(30) := 'PROJECT_DETAILS';

    l_project_detail_status_map project_detail_status_type := get_project_detail_status_map();

  BEGIN

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message => 'Creating ' || K_DESTINATION_TABLE_NAME || ' record for legacy project detail with ID ' || p_legacy_project_detail_id
    );

    INSERT INTO ${datasource.user}.project_details(
      project_id
    , status
    , version
    , is_current_version
    , created_by_wua
    , submitted_datetime
    , submitted_by_wua
    , is_migrated
    , created_datetime
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
    , p_submitted_datetime
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
    , p_system_message => 'Created new ' || K_DESTINATION_TABLE_NAME || ' record with ID ' || po_new_project_detail_id
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

    K_DESTINATION_TABLE_NAME CONSTANT VARCHAR2(30) := 'PROJECT_PUBLISHING_DETAILS';

    l_new_publish_detail_id ${datasource.user}.project_publishing_details.id%TYPE;

  BEGIN

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message => 'Creating ' || K_DESTINATION_TABLE_NAME || ' record for legacy project detail with ID ' || p_legacy_project_detail_id
    );

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
    , p_system_message => 'Created new ' || K_DESTINATION_TABLE_NAME || ' record with ID ' || l_new_publish_detail_id
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
    Procedure to create a record in the project_operators table in the new service model.
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_new_project_detail_id The detail id the publish record should be associated to
   */
  PROCEDURE create_project_operator_record(
    p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , p_new_project_detail_id IN ${datasource.user}.project_details.id%TYPE
  )
  IS

    K_DESTINATION_TABLE_NAME CONSTANT VARCHAR2(30) := 'PROJECT_OPERATORS';

    l_operator_org_grp_id ${datasource.user}.project_operators.operator_org_grp_id%TYPE;
    l_new_project_operator_id ${datasource.user}.project_operators.id%TYPE;

  BEGIN

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message => 'Creating ' || K_DESTINATION_TABLE_NAME || ' record for legacy project detail with ID ' || p_legacy_project_detail_id
    );

    SELECT lpd.operator_org_group_id
    INTO l_operator_org_grp_id
    FROM ${datasource.migration-user}.legacy_project_data lpd
    WHERE lpd.legacy_project_detail_id = p_legacy_project_detail_id;

    INSERT INTO ${datasource.user}.project_operators(
      project_detail_id
    , operator_org_grp_id
    )
    VALUES(
      p_new_project_detail_id
    , l_operator_org_grp_id
    )
    RETURNING id INTO l_new_project_operator_id;

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message => 'Created new ' || K_DESTINATION_TABLE_NAME || ' record with id ' || l_new_project_operator_id
    );

  EXCEPTION WHEN OTHERS THEN
    raise_exception_with_trace(
      p_message_prefix => 'ERROR in create_project_operator_record(' || CHR(10)
        || '  p_legacy_project_detail_id => ' || p_legacy_project_detail_id || CHR(10)
        || ', p_new_project_detail_id => ' || p_new_project_detail_id || CHR(10)
        || ')'
    );
  END create_project_operator_record;

  /**
    Procedure to create a record in the project_information table in the new service model.
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_new_project_detail_id The detail id the publish record should be associated to
   */
  PROCEDURE create_project_info_record(
    p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , p_new_project_detail_id IN ${datasource.user}.project_details.id%TYPE
  )
  IS

    K_DESTINATION_TABLE_NAME CONSTANT VARCHAR2(30) := 'PROJECT_INFORMATION';

    l_new_project_information_id ${datasource.user}.project_information.id%TYPE;

    l_field_stage ${datasource.migration-user}.legacy_project_data.field_stage%TYPE;
    l_project_title ${datasource.migration-user}.legacy_project_data.project_title%TYPE;
    l_project_summary ${datasource.migration-user}.legacy_project_data.project_summary%TYPE;
    l_contact_name ${datasource.migration-user}.legacy_project_data.project_contact_name%TYPE;
    l_contact_phone_number ${datasource.migration-user}.legacy_project_data.project_contact_tel_number%TYPE;
    l_contact_job_title ${datasource.migration-user}.legacy_project_data.project_contact_job_title%TYPE;
    l_contact_email_address ${datasource.migration-user}.legacy_project_data.project_contact_email_address%TYPE;
    l_first_prod_date_quarter ${datasource.migration-user}.legacy_project_data.first_production_quarter%TYPE;
    l_first_prod_date_year ${datasource.migration-user}.legacy_project_data.first_production_year%TYPE;

  BEGIN

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message => 'Creating ' || K_DESTINATION_TABLE_NAME || ' record for legacy project detail with ID ' || p_legacy_project_detail_id
    );

    SELECT
       -- map CURRENT_PROJECT to DEVELOPMENT but leave every other option
      DECODE(lpd.field_stage, 'CURRENT_PROJECT', 'DEVELOPMENT', lpd.field_stage)
    , lpd.project_title
    , lpd.project_summary
    , lpd.project_contact_name
    , lpd.project_contact_tel_number
    , lpd.project_contact_job_title
    , lpd.project_contact_email_address
    , DECODE(
        lpd.first_production_quarter
      , 0, NULL -- map 0 to NULL to cater for project having NS selected
      , NULL, NULL -- map NULL to NULL
      , 'Q' || lpd.first_production_quarter -- map the quarter value to Q1, Q2, Q3, Q4
      )
    , lpd.first_production_year
    INTO
      l_field_stage
    , l_project_title
    , l_project_summary
    , l_contact_name
    , l_contact_phone_number
    , l_contact_job_title
    , l_contact_email_address
    , l_first_prod_date_quarter
    , l_first_prod_date_year
    FROM ${datasource.migration-user}.legacy_project_data lpd
    WHERE lpd.legacy_project_detail_id = p_legacy_project_detail_id;

    INSERT INTO ${datasource.user}.project_information(
      project_detail_id
    , field_stage
    , project_title
    , project_summary
    , contact_name
    , phone_number
    , job_title
    , email_address
    , first_production_date_quarter
    , first_production_date_year
    )
    VALUES(
      p_new_project_detail_id
    , l_field_stage
    , l_project_title
    , l_project_summary
    , l_contact_name
    , l_contact_phone_number
    , l_contact_job_title
    , l_contact_email_address
    , l_first_prod_date_quarter
    , l_first_prod_date_year
    )
    RETURNING id INTO l_new_project_information_id;

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message => 'Created new ' || K_DESTINATION_TABLE_NAME || ' record with id ' || l_new_project_information_id
    );

  EXCEPTION WHEN OTHERS THEN
    raise_exception_with_trace(
      p_message_prefix => 'ERROR in create_project_info_record(' || CHR(10)
        || '  p_legacy_project_detail_id => ' || p_legacy_project_detail_id || CHR(10)
        || ', p_new_project_detail_id => ' || p_new_project_detail_id || CHR(10)
        || ')'
    );
  END create_project_info_record;

  /**
    Utility function to lookup try to find a matching devuk field based on the free text value
    entered on the legacy project.
    @param p_legacy_manual_entry_field The free text field value entered on the legacy project
    @return The ID of a devuk field whose name matches exactly p_legacy_manual_entry_field
   */
  FUNCTION lookup_field_id_from_freetext(
    p_legacy_manual_entry_field IN ${datasource.migration-user}.legacy_project_data.manual_field_name%TYPE
  ) RETURN ${datasource.user}.project_locations.field_id%TYPE
  IS

    l_matched_devuk_field_id ${datasource.user}.project_locations.field_id%TYPE;

  BEGIN

    BEGIN

      SELECT f.field_identifier
      INTO l_matched_devuk_field_id
      FROM devukmgr.fields f
      WHERE LOWER(f.name) = LOWER(p_legacy_manual_entry_field);

    EXCEPTION WHEN NO_DATA_FOUND THEN
      l_matched_devuk_field_id := NULL;
    END;

    RETURN l_matched_devuk_field_id;

  END lookup_field_id_from_freetext;

  /**
    Utility procedure to sanitise the legacy devuk field data into a format for the new service model.
    The rules are:
    - If both po_legacy_devuk_field_id and po_legacy_manual_field_name are populated, then NULL po_legacy_manual_field_name
      so we only persist po_legacy_devuk_field_id in the new model
    - If po_legacy_manual_field_name is populated then try to lookup a devuk field id which exactly matches po_legacy_manual_field_name
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param po_legacy_devuk_field_id The devuk field id from the legacy model
    @param po_legacy_manual_field_name The manual field name from the legacy model
   */
  PROCEDURE sanitise_legacy_field_inputs(
    p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , po_legacy_devuk_field_id IN OUT ${datasource.migration-user}.legacy_project_data.devuk_field_id%TYPE
  , po_legacy_manual_field_name IN OUT ${datasource.migration-user}.legacy_project_data.manual_field_name%TYPE
  )
  IS

    l_devuk_field_id_lookup ${datasource.user}.project_locations.field_id%TYPE;

  BEGIN

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message =>
        'Starting sanitise_legacy_field_inputs for legacy project detail with ID ' || p_legacy_project_detail_id
        || 'with inputs po_legacy_devuk_field_id => ' || COALESCE(TO_CHAR(po_legacy_devuk_field_id), 'NULL')
        || ', po_legacy_manual_field_name => ' || COALESCE(po_legacy_manual_field_name, 'NULL')
    );

    IF po_legacy_devuk_field_id IS NOT NULL THEN

      -- If operator has selected the devuk field, don't set the manual field name.
      -- There are hundreds of cases on live where both are set and we want to take the devuk id over the free text name.
      po_legacy_manual_field_name := NULL;

    ELSIF po_legacy_manual_field_name IS NOT NULL THEN

      -- If a manual field name is entered and we don't have a devuk field id, try an exact match lookup from devuk
      -- to see if we can find a field with the same name. Preference would be to store the devuk id if possible.
      l_devuk_field_id_lookup := lookup_field_id_from_freetext(
        p_legacy_manual_entry_field => po_legacy_manual_field_name
      );

      IF l_devuk_field_id_lookup IS NOT NULL THEN
        po_legacy_devuk_field_id := l_devuk_field_id_lookup;
        po_legacy_manual_field_name := NULL;
      END IF;

    END IF;

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message =>
          'Finished sanitise_legacy_field_inputs for legacy project detail with ID ' || p_legacy_project_detail_id
          || ' with outputs po_legacy_devuk_field_id => ' || COALESCE(TO_CHAR(po_legacy_devuk_field_id), 'NULL')
          || ', po_legacy_manual_field_name => ' || COALESCE(po_legacy_manual_field_name, 'NULL')
    );

  END sanitise_legacy_field_inputs;

  /**
    Utility function to take in a legacy water depth value and try to sanitise it for inclusion in the new model.
    We need to remove characters, spaces and any units associated to the legacy input.

    If the input contains a range of values e.g. 150-170 then we need to take the maximum value of the range.

    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_legacy_water_depth_value The legacy water depth value we want to sanitise
   */
  FUNCTION sanitise_legacy_water_depth(
    p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , p_legacy_water_depth_value IN ${datasource.migration-user}.legacy_project_data.water_depth%TYPE
  ) RETURN ${datasource.user}.project_locations.maximum_water_depth%TYPE
  IS

    l_sanitised_water_depth_value ${datasource.migration-user}.legacy_project_data.water_depth%TYPE;

  BEGIN

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message =>
          'Starting sanitise_legacy_water_depth_input for legacy project detail with ID ' || p_legacy_project_detail_id
          || ' with input p_legacy_water_depth_value => ' || COALESCE(p_legacy_water_depth_value, 'NULL')
    );

    -- remove any text characters, spaces or commas from the legacy value
    SELECT REGEXP_REPLACE(LOWER(p_legacy_water_depth_value), '[a-z]| |,', '')
    INTO l_sanitised_water_depth_value
    FROM dual;

    -- if the string contains a hyphen then we have a range of values
    IF INSTR(l_sanitised_water_depth_value, '-') > 0 THEN

      -- split on the hyphen character and get the MAX value from the list
      SELECT MAX(t.column_value)
      INTO l_sanitised_water_depth_value
      FROM TABLE(envmgr.st.split(l_sanitised_water_depth_value, '-')) t;

    END IF;

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message =>
          'Finished sanitise_legacy_water_depth_input for legacy project detail with ID ' || p_legacy_project_detail_id
          || ' with sanitised value ' || COALESCE(l_sanitised_water_depth_value, 'NULL')
    );

    -- return either a number or NULL
    RETURN envmgr.st.to_number_safe(l_sanitised_water_depth_value);

  END sanitise_legacy_water_depth;

  /**
    Utility function to get the water depth value for the new model. This will also populate the
    water_depth_migration_mapping table with the before and after value so we can see how it was sanitised.
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_legacy_water_depth_value The legacy water depth value we want to sanitise
   */
  FUNCTION get_water_depth_value(
    p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , p_legacy_water_depth_value IN ${datasource.migration-user}.legacy_project_data.water_depth%TYPE
  ) RETURN NUMBER
  IS

    PRAGMA AUTONOMOUS_TRANSACTION;

    K_IS_MIGRATABLE_FALSE CONSTANT ${datasource.migration-user}.water_depth_migration_mapping.is_migratable%TYPE := 0;
    K_IS_MIGRATABLE_TRUE CONSTANT ${datasource.migration-user}.water_depth_migration_mapping.is_migratable%TYPE := 1;

    l_sanitised_water_depth_value ${datasource.user}.project_locations.maximum_water_depth%TYPE;
    l_is_water_depth_migratable ${datasource.migration-user}.water_depth_migration_mapping.is_migratable%TYPE;

  BEGIN

    -- merge statement so we can do rerun of project migrations and not get duplicate rows
    MERGE INTO ${datasource.migration-user}.water_depth_migration_mapping wdmm
    USING(
      SELECT p_legacy_project_detail_id legacy_project_detail_id
      FROM dual
    ) t
    ON (t.legacy_project_detail_id = wdmm.legacy_project_detail_id)
    WHEN MATCHED THEN
      UPDATE SET
        original_water_depth_value = p_legacy_water_depth_value
      , is_migratable = K_IS_MIGRATABLE_FALSE
    WHEN NOT MATCHED THEN
      INSERT (
        legacy_project_detail_id
      , original_water_depth_value
      , is_migratable
      )
      VALUES(
        p_legacy_project_detail_id
      , p_legacy_water_depth_value
      , K_IS_MIGRATABLE_FALSE
      );

    IF p_legacy_water_depth_value IS NOT NULL THEN

      l_sanitised_water_depth_value := sanitise_legacy_water_depth(
        p_legacy_project_detail_id => p_legacy_project_detail_id
      , p_legacy_water_depth_value => p_legacy_water_depth_value
      );

      -- if after sanitisation we have not managed to get a number
      -- then mark the mapping table as K_IS_MIGRATABLE_FALSE and NULL
      -- will be returned for migration
      SELECT DECODE(
        l_sanitised_water_depth_value
      , NULL, K_IS_MIGRATABLE_FALSE
      , K_IS_MIGRATABLE_TRUE
      )
      INTO l_is_water_depth_migratable
      FROM dual;

    ELSE
      -- if p_legacy_water_depth_value IS NULL then we will migrate NULL to new model
      l_sanitised_water_depth_value := NULL;
      l_is_water_depth_migratable := K_IS_MIGRATABLE_TRUE;

    END IF;

    UPDATE ${datasource.migration-user}.water_depth_migration_mapping wdmm
    SET
      wdmm.sanitised_water_depth_value = l_sanitised_water_depth_value
    , wdmm.is_migratable = l_is_water_depth_migratable
    WHERE wdmm.legacy_project_detail_id = p_legacy_project_detail_id;

    COMMIT;

    RETURN l_sanitised_water_depth_value;

  END get_water_depth_value;

  /**
    Procedure to create a record in the project_locations table in the new service model.
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_new_project_detail_id The detail id the publish record should be associated to
    @return The id of the newly created PROJECT_LOCATIONS record
   */
  FUNCTION create_project_location_record(
    p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , p_new_project_detail_id IN ${datasource.user}.project_details.id%TYPE
  ) RETURN ${datasource.user}.project_locations.id%TYPE
  IS

    K_DESTINATION_TABLE_NAME CONSTANT VARCHAR2(30) := 'PROJECT_LOCATIONS';

    l_new_project_location_id ${datasource.user}.project_locations.id%TYPE;

    l_devuk_field_id ${datasource.migration-user}.legacy_project_data.devuk_field_id%TYPE;
    l_manual_field_name ${datasource.migration-user}.legacy_project_data.manual_field_name%TYPE;
    l_field_type ${datasource.migration-user}.legacy_project_data.field_type%TYPE;
    l_fdp_approved ${datasource.migration-user}.legacy_project_data.fdp_approved%TYPE;

    l_legacy_water_depth ${datasource.migration-user}.legacy_project_data.water_depth%TYPE;
    l_sanitised_water_depth ${datasource.user}.project_locations.maximum_water_depth%TYPE;

  BEGIN

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message => 'Creating ' || K_DESTINATION_TABLE_NAME || ' record for legacy project detail with ID ' || p_legacy_project_detail_id
    );

    SELECT
      lpd.devuk_field_id
    , lpd.manual_field_name
    , lpd.field_type
    , lpd.fdp_approved
    , lpd.water_depth
    INTO
      l_devuk_field_id
    , l_manual_field_name
    , l_field_type
    , l_fdp_approved
    , l_legacy_water_depth
    FROM ${datasource.migration-user}.legacy_project_data lpd
    WHERE lpd.legacy_project_detail_id = p_legacy_project_detail_id;

    sanitise_legacy_field_inputs(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , po_legacy_devuk_field_id => l_devuk_field_id
    , po_legacy_manual_field_name => l_manual_field_name
    );

    l_sanitised_water_depth := get_water_depth_value(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_legacy_water_depth_value => l_legacy_water_depth
    );

    INSERT INTO ${datasource.user}.project_locations(
      project_detail_id
    , field_id
    , manual_field_name
    , maximum_water_depth
    , field_type
    , approved_fdp
    )
    VALUES(
      p_new_project_detail_id
    , l_devuk_field_id
    , l_manual_field_name
    , l_sanitised_water_depth
    , l_field_type
    , l_fdp_approved
    )
    RETURNING id INTO l_new_project_location_id;

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message => 'Created new ' || K_DESTINATION_TABLE_NAME || ' record with id ' || l_new_project_location_id
    );

    RETURN l_new_project_location_id;

  EXCEPTION WHEN OTHERS THEN
    raise_exception_with_trace(
      p_message_prefix => 'ERROR in create_project_location_record(' || CHR(10)
        || '  p_legacy_project_detail_id => ' || p_legacy_project_detail_id || CHR(10)
        || ', p_new_project_detail_id => ' || p_new_project_detail_id || CHR(10)
        || ')'
    );
  END create_project_location_record;

  /**
    Utility function to sanitise the legacy location input and extract a list of block references
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_legacy_location_input The legacy location input from the p_legacy_project_detail_id that we want to process
    @return a list of block references extracted from the legacy location input
   */
  FUNCTION sanitise_legacy_location_input(
    p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , p_legacy_location_input IN ${datasource.migration-user}.legacy_project_data.location%TYPE
  ) RETURN bpmmgr.varchar2_list_type
  IS

    l_quadrant_no ${datasource.user}.project_location_blocks.quadrant_no%TYPE;
    l_block_no ${datasource.user}.project_location_blocks.block_no%TYPE;
    l_block_suffix ${datasource.user}.project_location_blocks.block_suffix%TYPE;

    l_block_list bpmmgr.varchar2_list_type := bpmmgr.varchar2_list_type();

  BEGIN

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message =>
          'Starting sanitise_legacy_location_input for legacy project detail with ID ' || p_legacy_project_detail_id
          || ' with input p_legacy_location_input => ' || COALESCE(p_legacy_location_input, 'NULL')
    );

    FOR potential_block_ref IN (
      -- convert all spaces to commas so we can process each item in the string individually
      SELECT t.column_value reference
      FROM TABLE(envmgr.st.split(REPLACE(p_legacy_location_input, ' ', ','), ',')) t
    )
    LOOP

      -- attempt to extract the quadrant no, block number and suffix from the
      -- potential block ref string
      BEGIN

        pedmgr.ped_utils.block_ref_split (
          p_block_ref => potential_block_ref.reference
        , p_quadrant_no => l_quadrant_no
        , p_block_no => l_block_no
        , p_suffix => l_block_suffix
        );

      EXCEPTION WHEN OTHERS THEN

        l_quadrant_no := NULL;
        l_block_no := NULL;
        l_block_suffix := NULL;

      END;

      -- the quadrant no and block no are the two mandatory parts of a block reference
      -- so if both are present then we have a valid block reference and should add it to the list
      IF l_quadrant_no IS NOT NULL AND l_block_no IS NOT NULL THEN
        l_block_list.EXTEND;
        l_block_list(l_block_list.COUNT) := potential_block_ref.reference;
      END IF;

    END LOOP;

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message =>
        'Finished sanitise_legacy_location_input for legacy project detail with ID ' || p_legacy_project_detail_id
        || ' with sanitised value ' || COALESCE(envmgr.st.join(l_block_list, ','), 'NULL')
    );

    RETURN l_block_list;

  END sanitise_legacy_location_input;

  /**
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_legacy_location_input The legacy location input from the p_legacy_project_detail_id that we want to process
    @return a list of block references extracted from the legacy location input
   */
  FUNCTION get_location_block_list(
    p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , p_legacy_location_input ${datasource.migration-user}.legacy_project_data.location%TYPE
  ) RETURN bpmmgr.varchar2_list_type
  IS

    PRAGMA AUTONOMOUS_TRANSACTION;

    K_IS_MIGRATABLE_FALSE CONSTANT ${datasource.migration-user}.location_migration_mapping.is_migratable%TYPE := 0;
    K_IS_MIGRATABLE_TRUE CONSTANT ${datasource.migration-user}.location_migration_mapping.is_migratable%TYPE := 1;

    l_sanitised_location_list bpmmgr.varchar2_list_type;
    l_is_location_migratable ${datasource.migration-user}.location_migration_mapping.is_migratable%TYPE;

  BEGIN

    -- merge statement so we can do rerun of project migrations and not get duplicate rows
    MERGE INTO ${datasource.migration-user}.location_migration_mapping lmm
    USING(
      SELECT p_legacy_project_detail_id legacy_project_detail_id
      FROM dual
    ) t
    ON (t.legacy_project_detail_id = lmm.legacy_project_detail_id)
    WHEN MATCHED THEN
      UPDATE SET
        original_location_value = p_legacy_location_input
      , is_migratable = K_IS_MIGRATABLE_FALSE
    WHEN NOT MATCHED THEN
      INSERT (
        legacy_project_detail_id
      , original_location_value
      , is_migratable
      )
      VALUES(
        p_legacy_project_detail_id
      , p_legacy_location_input
      , K_IS_MIGRATABLE_FALSE
      );

    IF p_legacy_location_input IS NOT NULL THEN

      l_sanitised_location_list := sanitise_legacy_location_input(
        p_legacy_project_detail_id => p_legacy_project_detail_id
      , p_legacy_location_input => p_legacy_location_input
      );

      -- if we have managed to find blocks to migrate
      IF l_sanitised_location_list.COUNT != 0 THEN
        l_is_location_migratable := K_IS_MIGRATABLE_TRUE;
      ELSE
        l_is_location_migratable := K_IS_MIGRATABLE_FALSE;
      END IF;

    ELSE

      -- set to empty list
      l_sanitised_location_list := bpmmgr.varchar2_list_type();
      l_is_location_migratable := K_IS_MIGRATABLE_TRUE;

    END IF;

    UPDATE ${datasource.migration-user}.location_migration_mapping lmm
    SET
      -- persist a csv of sanitised blocks for easy viewing in the logs
      lmm.sanitised_location_value = envmgr.st.join(l_sanitised_location_list, ',')
    , lmm.is_migratable = l_is_location_migratable
    WHERE lmm.legacy_project_detail_id = p_legacy_project_detail_id;

    COMMIT;

    RETURN l_sanitised_location_list;

  END get_location_block_list;

  /**
    Procedure to insert zero or more location block records associated to p_new_location_id into the new model.
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_new_location_id The id of the PROJECT_LOCATION_BLOCKS record that we want the blocks associated to
   */
  PROCEDURE create_location_block_records(
    p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , p_new_location_id IN ${datasource.user}.project_details.id%TYPE
  )
  IS

    K_DESTINATION_TABLE_NAME CONSTANT VARCHAR2(30) := 'PROJECT_LOCATION_BLOCKS';

    l_legacy_location_value ${datasource.migration-user}.legacy_project_data.location%TYPE;
    l_sanitised_block_list bpmmgr.varchar2_list_type;

    l_new_location_block_id ${datasource.user}.project_location_blocks.id%TYPE;
    l_block_ref ${datasource.user}.project_location_blocks.block_ref%TYPE;
    l_quadrant_no ${datasource.user}.project_location_blocks.quadrant_no%TYPE;
    l_block_no ${datasource.user}.project_location_blocks.block_no%TYPE;
    l_block_suffix ${datasource.user}.project_location_blocks.block_suffix%TYPE;

  BEGIN

    SELECT lpd.location
    INTO l_legacy_location_value
    FROM ${datasource.migration-user}.legacy_project_data lpd
    WHERE lpd.legacy_project_detail_id = p_legacy_project_detail_id;

    l_sanitised_block_list := get_location_block_list(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_legacy_location_input => l_legacy_location_value
    );

    IF l_sanitised_block_list.COUNT > 0 THEN

      FOR block_idx IN l_sanitised_block_list.FIRST..l_sanitised_block_list.COUNT LOOP

        log_project_detail_migration(
          p_legacy_project_detail_id => p_legacy_project_detail_id
        , p_system_message => 'Creating ' || K_DESTINATION_TABLE_NAME || ' record for legacy project detail with ID ' || p_legacy_project_detail_id
        );

        l_block_ref := l_sanitised_block_list(block_idx);

        pedmgr.ped_utils.block_ref_split (
          p_block_ref => l_block_ref
        , p_quadrant_no => l_quadrant_no
        , p_block_no => l_block_no
        , p_suffix => l_block_suffix
        );

        INSERT INTO ${datasource.user}.project_location_blocks(
          project_location_id
        , block_ref
        , block_no
        , quadrant_no
        , block_suffix
        )
        VALUES(
          p_new_location_id
        , l_block_ref
        , l_block_no
        , l_quadrant_no
        , l_block_suffix
        )
        RETURNING id INTO l_new_location_block_id;

        log_project_detail_migration(
          p_legacy_project_detail_id => p_legacy_project_detail_id
        , p_system_message => 'Created new ' || K_DESTINATION_TABLE_NAME || ' record with id ' || l_new_location_block_id
        );

      END LOOP;

    END IF;

  EXCEPTION WHEN OTHERS THEN
    raise_exception_with_trace(
      p_message_prefix => 'ERROR in create_location_block_records(' || CHR(10)
        || '  p_legacy_project_detail_id => ' || p_legacy_project_detail_id || CHR(10)
        || ', p_new_location_id => ' || p_new_location_id || CHR(10)
        || ')'
    );
  END create_location_block_records;

  /**
    Procedure to create a record in the awarded_contracts table in the new service model.
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_new_project_detail_id The detail id the publish record should be associated to
   */
  PROCEDURE create_awarded_contracts(
    p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , p_new_project_detail_id IN ${datasource.user}.project_details.id%TYPE
  )
  IS

    K_DESTINATION_TABLE_NAME CONSTANT VARCHAR2(30) := 'AWARDED_CONTRACTS';

  BEGIN

    FOR legacy_contract IN (
      SELECT
        lpc.contractor_name
      , lpc.description_of_work
      , lpc.date_awarded
      , DECODE(
          lpc.contract_band
        , 'SMALL', 'LESS_THAN_25M'
        , 'MEDIUM', 'LESS_THAN_25M'
        , 'LARGE', 'GREATER_THAN_OR_EQUAL_TO_25M'
        , NULL
        ) contract_band
      , DECODE(
          lpc.contact_name
          -- a legacy data patch put "Not Specified" as a default value which
          -- we don't want to copy over to the new model
        , 'Not Specified', NULL
        , lpc.contact_name
        ) contact_name
      , lpc.contact_telephone_no
      , lpc.contact_email_address
      , ROWNUM idx
      , COUNT(*) OVER (PARTITION BY lpc.legacy_project_detail_id) total_contracts_for_detail
      FROM ${datasource.migration-user}.legacy_project_contracts lpc
      WHERE lpc.legacy_project_detail_id = p_legacy_project_detail_id
    )
    LOOP

      DECLARE

        K_AWARDED_CONTRACT_PROGRESS CONSTANT VARCHAR2(4000) := legacy_contract.idx || '/' || legacy_contract.total_contracts_for_detail;

        l_new_awarded_contract_id ${datasource.user}.awarded_contracts.id%TYPE;

      BEGIN

        log_project_detail_migration(
          p_legacy_project_detail_id => p_legacy_project_detail_id
        , p_system_message => 'Creating ' || K_DESTINATION_TABLE_NAME || ' record (' || K_AWARDED_CONTRACT_PROGRESS ||
                              ') for legacy project detail with ID ' || p_legacy_project_detail_id
        );

        INSERT INTO ${datasource.user}.awarded_contracts(
          project_detail_id
        , contractor_name
        , description_of_work
        , date_awarded
        , contract_band
        , contact_name
        , phone_number
        , email_address
        )
        VALUES(
          p_new_project_detail_id
        , legacy_contract.contractor_name
        , legacy_contract.description_of_work
        , legacy_contract.date_awarded
        , legacy_contract.contract_band
        , legacy_contract.contact_name
        , legacy_contract.contact_telephone_no
        , legacy_contract.contact_email_address
        )
        RETURNING id INTO l_new_awarded_contract_id;

        log_project_detail_migration(
          p_legacy_project_detail_id => p_legacy_project_detail_id
        , p_system_message => 'Created new ' || K_DESTINATION_TABLE_NAME || ' record (' || K_AWARDED_CONTRACT_PROGRESS ||
                              ') with id ' || l_new_awarded_contract_id
        );

      END;

    END LOOP;

  EXCEPTION WHEN OTHERS THEN
    raise_exception_with_trace(
      p_message_prefix => 'ERROR in create_awarded_contracts(' || CHR(10)
        || '  p_legacy_project_detail_id => ' || p_legacy_project_detail_id || CHR(10)
        || ', p_new_project_detail_id => ' || p_new_project_detail_id || CHR(10)
        || ')'
    );
  END create_awarded_contracts;

  /**
    Procedure to create a record in the collaboration_opportunities table in the new service model.
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_new_project_detail_id The detail id the publish record should be associated to
   */
  PROCEDURE create_collab_opportunities(
    p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , p_new_project_detail_id IN ${datasource.user}.project_details.id%TYPE
  )
  IS

    K_DESTINATION_TABLE_NAME CONSTANT VARCHAR2(30) := 'COLLABORATION_OPPORTUNITIES';

  BEGIN

    FOR legacy_opportunity IN(
      SELECT
        lpc.description_of_work
      , lpc.contact_name
      , lpc.contact_telephone_no
      , lpc.contact_email_address
      , ROWNUM idx
      , COUNT(*) OVER (PARTITION BY lpc.legacy_project_detail_id) total_contracts_for_detail
      FROM ${datasource.migration-user}.legacy_project_challenges lpc
      WHERE lpc.legacy_project_detail_id = p_legacy_project_detail_id
    )
    LOOP

      DECLARE

        K_COLLAB_OPPORTUNITY_PROGRESS CONSTANT VARCHAR2(4000) := legacy_opportunity.idx || '/' || legacy_opportunity.total_contracts_for_detail;

        l_new_collab_opportunity_id ${datasource.user}.collaboration_opportunities.id%TYPE;

      BEGIN

        log_project_detail_migration(
          p_legacy_project_detail_id => p_legacy_project_detail_id
        , p_system_message => 'Creating ' || K_DESTINATION_TABLE_NAME || ' record (' || K_COLLAB_OPPORTUNITY_PROGRESS ||
                              ') for legacy project detail with ID ' || p_legacy_project_detail_id
        );

        INSERT INTO ${datasource.user}.collaboration_opportunities(
          project_detail_id
        , description_of_work
        , contact_name
        , phone_number
        , email_address
        )
        VALUES(
          p_new_project_detail_id
        , legacy_opportunity.description_of_work
        , legacy_opportunity.contact_name
        , legacy_opportunity.contact_telephone_no
        , legacy_opportunity.contact_email_address
        )
        RETURNING id INTO l_new_collab_opportunity_id;

        log_project_detail_migration(
          p_legacy_project_detail_id => p_legacy_project_detail_id
        , p_system_message => 'Created new ' || K_DESTINATION_TABLE_NAME || ' record (' || K_COLLAB_OPPORTUNITY_PROGRESS ||
                              ') with id ' || l_new_collab_opportunity_id
        );

      END;

    END LOOP;

  EXCEPTION WHEN OTHERS THEN
    raise_exception_with_trace(
      p_message_prefix => 'ERROR in create_collab_opportunities(' || CHR(10)
        || '  p_legacy_project_detail_id => ' || p_legacy_project_detail_id || CHR(10)
        || ', p_new_project_detail_id => ' || p_new_project_detail_id || CHR(10)
        || ')'
    );
  END create_collab_opportunities;

  /**
    Procedure to create a record in the project_task_list_setup table in the new service model.
    Only the awarded contracts and collaboration opportunities section will be added to the task list
    setup as these are the only sections that carry over into the new service model.
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_new_project_detail_id The detail id the publish record should be associated to
   */
  PROCEDURE create_project_setup_record(
    p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , p_new_project_detail_id IN ${datasource.user}.project_details.id%TYPE
  )
  IS

    K_DESTINATION_TABLE_NAME CONSTANT VARCHAR2(30) := 'PROJECT_TASK_LIST_SETUP';

    K_AWARDED_CONTRACTS_YES CONSTANT VARCHAR2(4000) := 'AWARDED_CONTRACTS_YES';
    K_AWARDED_CONTRACTS_NO CONSTANT VARCHAR2(4000) := 'AWARDED_CONTRACTS_NO';
    K_AWARDED_CONTRACTS_SECTION CONSTANT VARCHAR2(4000) := 'AWARDED_CONTRACTS';

    K_COLLABORATION_OPS_YES CONSTANT VARCHAR2(4000) := 'COLLABORATION_OPPORTUNITIES_YES';
    K_COLLABORATION_OPS_NO CONSTANT VARCHAR2(4000) := 'COLLABORATION_OPPORTUNITIES_NO';
    K_COLLABORATION_OPS_SECTION CONSTANT VARCHAR2(4000) := 'COLLABORATION_OPPORTUNITIES';

    l_awarded_contracts_answer ${datasource.user}.project_task_list_setup.task_list_answers%TYPE;
    l_collaboration_ops_answer ${datasource.user}.project_task_list_setup.task_list_answers%TYPE;

    l_section_list bpmmgr.varchar2_list_type := bpmmgr.varchar2_list_type();

    l_new_project_setup_id ${datasource.user}.project_task_list_setup.id%TYPE;

  BEGIN

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message => 'Creating ' || K_DESTINATION_TABLE_NAME || ' record for legacy project detail with ID ' || p_legacy_project_detail_id
    );

    -- establish if we have any awarded contracts or collaboration opportunities
    -- for the given project detail. This will allow us to back fit the task list
    -- sections and answers the K_DESTINATION_TABLE_NAME record
    SELECT
      DECODE(
        (
          SELECT DECODE(COUNT(*), 0, 0, 1)
          FROM ${datasource.user}.awarded_contracts ac
          WHERE ac.project_detail_id = pd.id
        )
      , 0, K_AWARDED_CONTRACTS_NO
      , 1, K_AWARDED_CONTRACTS_YES
      ) awarded_contracts_answer
    , DECODE(
        (
          SELECT DECODE(COUNT(*), 0, 0, 1)
          FROM ${datasource.user}.collaboration_opportunities co
          WHERE co.project_detail_id = pd.id
        )
      , 0, K_COLLABORATION_OPS_NO
      , 1, K_COLLABORATION_OPS_YES
      ) collaboration_ops_answer
    INTO
      l_awarded_contracts_answer
    , l_collaboration_ops_answer
    FROM ${datasource.user}.project_details pd
    WHERE pd.id = p_new_project_detail_id;

    IF l_awarded_contracts_answer = K_AWARDED_CONTRACTS_YES THEN
      l_section_list.EXTEND;
      l_section_list(l_section_list.COUNT) := K_AWARDED_CONTRACTS_SECTION;
    END IF;

    IF l_collaboration_ops_answer = K_COLLABORATION_OPS_YES THEN
      l_section_list.EXTEND;
      l_section_list(l_section_list.COUNT) := K_COLLABORATION_OPS_SECTION;
    END IF;

    INSERT INTO ${datasource.user}.project_task_list_setup(
      project_detail_id
    , task_list_sections
    , task_list_answers
    )
    VALUES(
      p_new_project_detail_id
    , envmgr.st.join(l_section_list, ',')
    , l_awarded_contracts_answer || ',' || l_collaboration_ops_answer
    )
    RETURNING id INTO l_new_project_setup_id;

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message => 'Created new ' || K_DESTINATION_TABLE_NAME || ' record with id ' || l_new_project_setup_id
    );

  EXCEPTION WHEN OTHERS THEN
    raise_exception_with_trace(
      p_message_prefix => 'ERROR in create_project_setup_record(' || CHR(10)
        || '  p_legacy_project_detail_id => ' || p_legacy_project_detail_id || CHR(10)
        || ', p_new_project_detail_id => ' || p_new_project_detail_id || CHR(10)
        || ')'
    );
  END create_project_setup_record;

  /**
    Procedure to create records in all of the new form page section tables for a given legacy project detail
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_new_project_detail_id The detail id the publish record should be associated to
   */
  PROCEDURE migrate_project_form_data(
    p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , p_new_project_detail_id IN ${datasource.user}.project_details.id%TYPE
  )
  IS

    l_new_location_id ${datasource.user}.project_locations.id%TYPE;

  BEGIN

    create_project_operator_record(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_new_project_detail_id => p_new_project_detail_id
    );

    create_project_info_record(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_new_project_detail_id => p_new_project_detail_id
    );

    l_new_location_id := create_project_location_record(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_new_project_detail_id => p_new_project_detail_id
    );

    create_location_block_records(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_new_location_id => l_new_location_id
    );

    create_awarded_contracts(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_new_project_detail_id => p_new_project_detail_id
    );

    create_collab_opportunities(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_new_project_detail_id => p_new_project_detail_id
    );

    create_project_setup_record(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_new_project_detail_id => p_new_project_detail_id
    );

  END migrate_project_form_data;

  /**
    Procedure to create a record in the project_updates table in the new model for p_new_project_detail_id
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_new_project_detail_id The detail id the publish record should be associated to
   */
  PROCEDURE create_project_update_record(
    p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , p_new_project_detail_id IN ${datasource.user}.project_details.id%TYPE
  )
  IS

    K_DESTINATION_TABLE_NAME CONSTANT VARCHAR2(30) := 'PROJECT_UPDATES';

    l_new_project_update_id ${datasource.user}.project_updates.id%TYPE;
    l_prev_project_detail_id ${datasource.user}.project_details.id%TYPE;

  BEGIN

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message => 'Creating ' || K_DESTINATION_TABLE_NAME || ' record for legacy project detail with ID ' || p_legacy_project_detail_id
    );

    -- find the project detail id for the previous version of this project
    SELECT v.prev_project_detail_id
    INTO l_prev_project_detail_id
    FROM (
     SELECT
       pd.*
     , LAG(id, 1, 0) OVER (PARTITION BY project_id ORDER BY version) AS prev_project_detail_id
     FROM ${datasource.user}.project_details pd
    ) v
    WHERE v.id = p_new_project_detail_id;

    INSERT INTO ${datasource.user}.project_updates(
      from_project_detail_id
    , new_project_detail_id
    , update_type
    )
    VALUES(
      l_prev_project_detail_id
    , p_new_project_detail_id
    , K_OPERATOR_INITIATED_UPDATE
    )
    RETURNING id INTO l_new_project_update_id;

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message =>
        'Created new ' || K_DESTINATION_TABLE_NAME || ' record with id => ' || l_new_project_update_id ||
        ' and from_project_detail_id => ' || l_prev_project_detail_id
    );

  EXCEPTION WHEN OTHERS THEN
    raise_exception_with_trace(
      p_message_prefix => 'ERROR in create_project_update_record(' || CHR(10)
        || '  p_legacy_project_detail_id => ' || p_legacy_project_detail_id || CHR(10)
        || ', p_new_project_detail_id => ' || p_new_project_detail_id || CHR(10)
        || ')'
    );
  END create_project_update_record;

  /**
    Procedure to create a record in the project_archive_details table in the new model for p_new_project_detail_id
    @param p_legacy_project_detail_id The id of the legacy project detail record we are migrating
    @param p_new_project_detail_id The detail id the publish record should be associated to
   */
  PROCEDURE create_project_archive_record(
    p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , p_new_project_detail_id IN ${datasource.user}.project_details.id%TYPE
  )
  IS

    K_DESTINATION_TABLE_NAME CONSTANT VARCHAR2(30) := 'PROJECT_ARCHIVE_DETAILS';

    l_new_archive_detail_id ${datasource.user}.project_archive_details.id%TYPE;

  BEGIN

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message => 'Creating ' || K_DESTINATION_TABLE_NAME || ' record for legacy project detail with ID ' || p_legacy_project_detail_id
    );

    INSERT INTO ${datasource.user}.project_archive_details(
      project_detail_id
    , archive_reason
    )
    VALUES(
      p_new_project_detail_id
    , 'Project archived in legacy Project Pathfinder service'
    ) RETURNING id INTO l_new_archive_detail_id;

    log_project_detail_migration(
      p_legacy_project_detail_id => p_legacy_project_detail_id
    , p_system_message => 'Created new ' || K_DESTINATION_TABLE_NAME || ' record with id => ' || l_new_archive_detail_id
    );

  EXCEPTION WHEN OTHERS THEN
    raise_exception_with_trace(
      p_message_prefix => 'ERROR in create_project_archive_record(' || CHR(10)
        || '  p_legacy_project_detail_id => ' || p_legacy_project_detail_id || CHR(10)
        || ', p_new_project_detail_id => ' || p_new_project_detail_id || CHR(10)
        || ')'
    );
  END create_project_archive_record;

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

      ELSIF l_new_detail_status = K_NEW_ARCHIVED_STATUS THEN

        create_project_archive_record(
          p_legacy_project_detail_id => l_legacy_project_detail_id
        , p_new_project_detail_id => l_new_project_detail_id
        );

      END IF;

      /**
        If we have a version greater than 1 and our detail row is not archived then insert am
        update record. If the detail is archived that is not being consider an update in the model.
       */
      IF project_detail.version > 1 AND l_new_detail_status != K_NEW_ARCHIVED_STATUS THEN

        create_project_update_record(
          p_legacy_project_detail_id => l_legacy_project_detail_id
        , p_new_project_detail_id => l_new_project_detail_id
        );

      END IF;

      migrate_project_form_data(
        p_legacy_project_detail_id => l_legacy_project_detail_id
      , p_new_project_detail_id => l_new_project_detail_id
      );

      log_project_detail_migration(
        p_legacy_project_detail_id => l_legacy_project_detail_id
      , p_migration_status => K_COMPLETE_MIGRATION_STATUS
      , p_new_project_detail_id => l_new_project_detail_id
      , p_system_message => 'Completed migration of project detail record and associated project data'
      );

    END LOOP;

  END create_project_detail_records;

  PROCEDURE migrate_project(
    p_legacy_project_id IN decmgr.path_projects.id%TYPE
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
          || 'Failed with error:' || CHR(10) || dbms_utility.format_error_stack() || CHR(10) || dbms_utility.format_error_backtrace()
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

  /**
    Procedure to create a record in UNMAPPED_PROJECT_DATA. This will store the answer to a legacy question
    which is not mapped in the new model.
    @param p_legacy_project_id The id of the legacy project you want to migrate
    @param p_legacy_project_detail_id The id of the legacy project detail you want to migrate
    @param p_legacy_question_id The id of the legacy question from the UNMAPPED_PROJECT_DATA table
    @param p_legacy_answer The legacy answer to p_legacy_question_id that we want to store
   */
  PROCEDURE create_unmapped_data_record(
    p_legacy_project_id IN decmgr.path_projects.id%TYPE
  , p_legacy_project_detail_id IN decmgr.path_project_details.id%TYPE
  , p_legacy_question_id IN ${datasource.migration-user}.unmapped_project_data.legacy_question_id%TYPE
  , p_legacy_answer IN ${datasource.migration-user}.unmapped_project_data.legacy_answer%TYPE
  )
  IS

    l_new_project_id ${datasource.migration-user}.project_migration_log.new_project_id%TYPE;
    l_new_project_detail_id ${datasource.migration-user}.project_detail_migration_log.new_project_detail_id%TYPE;

    l_unmapped_project_data_id ${datasource.migration-user}.unmapped_project_data.id%TYPE;

  BEGIN

    IF p_legacy_answer IS NOT NULL THEN

      log_unmapped_detail_migration(
        p_legacy_project_detail_id => p_legacy_project_detail_id
      , p_system_message =>
          'Starting migrating for legacy question with p_legacy_question_id => ' || p_legacy_question_id ||
          ' and p_legacy_answer => ' || p_legacy_answer
      );

      SELECT
        pml.new_project_id
      , pdml.new_project_detail_id
      INTO
        l_new_project_id
      , l_new_project_detail_id
      FROM ${datasource.migration-user}.project_detail_migration_log pdml
      JOIN ${datasource.migration-user}.project_migration_log pml ON pml.legacy_project_id = pdml.legacy_project_id
      WHERE pdml.legacy_project_detail_id = p_legacy_project_detail_id;

      INSERT INTO ${datasource.migration-user}.unmapped_project_data(
        legacy_project_id
      , new_project_id
      , legacy_project_detail_id
      , new_project_detail_id
      , legacy_question_id
      , legacy_answer
      )
      VALUES(
        p_legacy_project_id
      , l_new_project_id
      , p_legacy_project_detail_id
      , l_new_project_detail_id
      , p_legacy_question_id
      , p_legacy_answer
      )
      RETURNING id INTO l_unmapped_project_data_id;

      log_unmapped_detail_migration(
        p_legacy_project_detail_id => p_legacy_project_detail_id
      , p_system_message =>
          'Completed migrating for legacy question with p_legacy_question_id => ' || p_legacy_question_id ||
          ' and p_legacy_answer => ' || p_legacy_answer || '. UNMAPPED_PROJECT_DATA record created with ID ' || l_unmapped_project_data_id
      );

    ELSE

      log_unmapped_detail_migration(
        p_legacy_project_detail_id => p_legacy_project_detail_id
      , p_system_message =>
          'Skipping migrating for legacy question with ID ' || p_legacy_question_id ||
          ' for legacy project detail with ID ' || p_legacy_project_detail_id || ' as p_legacy_answer IS NULL'
      );

    END IF;

  END create_unmapped_data_record;

  /**
    Procedure to create records in UNMAPPED_PROJECT_COMMENTS. This will store the details of the comment
    that was made on the project. This table will be used as a backup for when the legacy schema is torn down.
    @param p_legacy_project_id The id of the legacy project you want to migrate
   */
  PROCEDURE migrate_project_comments(
    p_legacy_project_id IN decmgr.path_projects.id%TYPE
  )
  IS

    l_unmapped_project_comment_id ${datasource.migration-user}.unmapped_project_comments.id%TYPE;

  BEGIN

    log_unmapped_project_migration(
      p_legacy_project_id => p_legacy_project_id
    , p_system_message => 'Starting unmapped project comment migration for legacy project with ID ' || p_legacy_project_id
    );

    FOR comment IN (
      SELECT
        ulc.comment_text
      , ulc.comment_datetime
      , ulc.commented_by_wua_id
      , ulc.comment_type
      FROM ${datasource.migration-user}.unmapped_legacy_comments ulc
      WHERE ulc.legacy_project_id = p_legacy_project_id
    )
    LOOP

      INSERT INTO ${datasource.migration-user}.unmapped_project_comments(
        legacy_project_id
      , comment_text
      , comment_datetime
      , commented_by_wua_id
      , comment_type
      )
      VALUES(
        p_legacy_project_id
      , comment.comment_text
      , comment.comment_datetime
      , comment.commented_by_wua_id
      , comment.comment_type
      ) RETURNING id INTO l_unmapped_project_comment_id;

      log_unmapped_project_migration(
        p_legacy_project_id => p_legacy_project_id
      , p_system_message => 'New UNMAPPED_PROJECT_COMMENTS record created with id ' || l_unmapped_project_comment_id ||
                            ' for legacy project with ID ' || p_legacy_project_id
      );

    END LOOP;

    log_unmapped_project_migration(
      p_legacy_project_id => p_legacy_project_id
    , p_system_message => 'Completed unmapped project comment migration for legacy project with ID ' || p_legacy_project_id
    );

  END migrate_project_comments;

  /**
    Procedure to migrate any unmapped project data for a single project into the new model.
    @param p_legacy_project_id The id of the legacy project you want to migrate
   */
  PROCEDURE migrate_unmapped_project_data(
    p_legacy_project_id IN decmgr.path_projects.id%TYPE
  )
  IS
  BEGIN

    SAVEPOINT sp_before_unmapped_migration;

    BEGIN

      log_unmapped_project_migration(
        p_legacy_project_id => p_legacy_project_id
      , p_migration_status => K_PROCESSING_MIGRATION_STATUS
      , p_system_message => 'Starting unmapped data migration for legacy project with ID ' || p_legacy_project_id
      );

      FOR project_detail IN (
        SELECT
          ppd.id legacy_detail_id
        , (
          SELECT upq.id
          FROM ${datasource.migration-user}.unmapped_project_questions upq
          WHERE upq.legacy_question_mnem = 'SUMMARY_OF_UPDATE'
        ) update_summary_question_id
        , (
          SELECT upq.id
          FROM ${datasource.migration-user}.unmapped_project_questions upq
          WHERE upq.legacy_question_mnem = 'ORIGINAL_PRODUCTION_QUARTER'
        ) orig_prod_quarter_question_id
        , (
          SELECT upq.id
          FROM ${datasource.migration-user}.unmapped_project_questions upq
          WHERE upq.legacy_question_mnem = 'ORIGINAL_PRODUCTION_YEAR'
        ) orig_prod_year_question_id
        , (
          SELECT upq.id
          FROM ${datasource.migration-user}.unmapped_project_questions upq
          WHERE upq.legacy_question_mnem = 'UNDER_CONSTRUCTION_FLAG'
        ) construction_flag_question_id
        , (
          SELECT upq.id
          FROM ${datasource.migration-user}.unmapped_project_questions upq
          WHERE upq.legacy_question_mnem = 'CONSTRUCTION_DATE'
        ) construction_date_question_id
        , ulpd.update_summary update_summary_answer
        , ulpd.original_production_quarter original_prod_quarter_answer
        , ulpd.original_production_year original_prod_year_answer
        , ulpd.under_construction_flag under_construction_flag_answer
        , ulpd.construction_date construction_date_answer
        FROM decmgr.path_projects pp
        JOIN decmgr.path_project_details ppd ON ppd.path_project_id = pp.id
        JOIN ${datasource.migration-user}.project_migration_log pml ON pml.legacy_project_id = pp.id
        JOIN ${datasource.migration-user}.unmapped_detail_migration_log udml ON udml.legacy_project_detail_id = ppd.id
        JOIN ${datasource.migration-user}.unmapped_legacy_project_data ulpd ON ulpd.legacy_project_detail_id = udml.legacy_project_detail_id
        WHERE pp.id = p_legacy_project_id
        -- where the unmapped status is pending
        AND udml.migration_status = K_PENDING_MIGRATION_STATUS
        -- but the actual project has successfully been migrated
        AND pml.migration_status = K_COMPLETE_MIGRATION_STATUS
        ORDER BY ppd.start_datetime
      )
      LOOP

        log_unmapped_detail_migration(
          p_legacy_project_detail_id => project_detail.legacy_detail_id
        , p_migration_status => K_PROCESSING_MIGRATION_STATUS
        , p_system_message =>
            'Starting unmapped data migrating for legacy project detail with ID ' || project_detail.legacy_detail_id
        );

        create_unmapped_data_record(
          p_legacy_project_id => p_legacy_project_id
        , p_legacy_project_detail_id => project_detail.legacy_detail_id
        , p_legacy_question_id => project_detail.update_summary_question_id
        , p_legacy_answer => project_detail.update_summary_answer
        );

        create_unmapped_data_record(
          p_legacy_project_id => p_legacy_project_id
        , p_legacy_project_detail_id => project_detail.legacy_detail_id
        , p_legacy_question_id => project_detail.orig_prod_quarter_question_id
        , p_legacy_answer => project_detail.original_prod_quarter_answer
        );

        create_unmapped_data_record(
          p_legacy_project_id => p_legacy_project_id
        , p_legacy_project_detail_id => project_detail.legacy_detail_id
        , p_legacy_question_id => project_detail.orig_prod_year_question_id
        , p_legacy_answer => project_detail.original_prod_year_answer
        );

        create_unmapped_data_record(
          p_legacy_project_id => p_legacy_project_id
        , p_legacy_project_detail_id => project_detail.legacy_detail_id
        , p_legacy_question_id => project_detail.construction_flag_question_id
        , p_legacy_answer => project_detail.under_construction_flag_answer
        );

        create_unmapped_data_record(
          p_legacy_project_id => p_legacy_project_id
        , p_legacy_project_detail_id => project_detail.legacy_detail_id
        , p_legacy_question_id => project_detail.construction_date_question_id
        , p_legacy_answer => project_detail.construction_date_answer
        );

        log_unmapped_detail_migration(
          p_legacy_project_detail_id => project_detail.legacy_detail_id
        , p_migration_status => K_COMPLETE_MIGRATION_STATUS
        , p_system_message =>
            'Completed unmapped data migrating for legacy project detail with ID ' || project_detail.legacy_detail_id
        );

      END LOOP;

      migrate_project_comments(
        p_legacy_project_id => p_legacy_project_id
      );

      log_unmapped_project_migration(
        p_legacy_project_id => p_legacy_project_id
      , p_migration_status => K_COMPLETE_MIGRATION_STATUS
      , p_system_message => 'Completed unmapped data migration for legacy project with ID ' || p_legacy_project_id
      );

      COMMIT;

    EXCEPTION WHEN OTHERS THEN

      ROLLBACK TO SAVEPOINT sp_before_unmapped_migration;

      log_unmapped_project_migration(
        p_legacy_project_id => p_legacy_project_id
      , p_migration_status => K_ERROR_MIGRATION_STATUS
      , p_system_message =>
          'Error migrating legacy unmapped project data for project with ID ' || p_legacy_project_id || '. '
          || 'Unmapped data migration for project has been rolled back.' || CHR(10)
          || 'Failed with error:' || CHR(10) || dbms_utility.format_error_stack() || CHR(10) || dbms_utility.format_error_backtrace()
      );

      reset_unmapped_detail_log(
        p_legacy_project_id => p_legacy_project_id
      , p_system_message =>
          'Error migrating unmapped project data. Unmapped data migration has been rolled back for this project. See project log for trace'
      );

    END;

  END migrate_unmapped_project_data;

  /**
    Procedure to migrate any unmapped project data for all projects into the new model.
   */
  PROCEDURE migrate_unmapped_project_data
  IS
  BEGIN

    populate_unmapped_data_logs();

    FOR project IN (
      SELECT upml.legacy_project_id id
      FROM ${datasource.migration-user}.unmapped_project_migration_log upml
      JOIN ${datasource.migration-user}.project_migration_log pml ON pml.legacy_project_id = upml.legacy_project_id
      -- where the unmapped status is pending
      WHERE upml.migration_status = K_PENDING_MIGRATION_STATUS
      -- but the actual project has successfully been migrated
      AND pml.migration_status = K_COMPLETE_MIGRATION_STATUS
      ORDER BY upml.legacy_project_id
    )
    LOOP

      migrate_unmapped_project_data(
        p_legacy_project_id => project.id
      );

    END LOOP;

  END migrate_unmapped_project_data;

END migration;