CREATE OR REPLACE PACKAGE ${datasource.migration-user}.migration AS

  K_PENDING_MIGRATION_STATUS CONSTANT VARCHAR2(4000) := 'PENDING';
  K_PROCESSING_MIGRATION_STATUS CONSTANT VARCHAR2(4000) := 'PROCESSING';
  K_COMPLETE_MIGRATION_STATUS CONSTANT VARCHAR2(4000) := 'COMPLETE';
  K_ERROR_MIGRATION_STATUS CONSTANT VARCHAR2(4000) := 'ERROR';

  K_NEW_PUBLISHED_STATUS CONSTANT VARCHAR2(4000) := 'PUBLISHED';
  K_NEW_ARCHIVED_STATUS CONSTANT VARCHAR2(4000) := 'ARCHIVED';

  TYPE project_detail_status_type IS TABLE OF VARCHAR2(4000)
  INDEX BY VARCHAR2(4000);

  /**
    Procedure to migrate a single legacy project into the new service model.
    @param p_legacy_project_id The id of the legacy project you want to migrate
   */
  PROCEDURE migrate_project(
    p_legacy_project_id IN NUMBER
  );

  /**
    Procedure to migrate all legacy projects into the new service model.
   */
  PROCEDURE migrate_projects;

END migration;