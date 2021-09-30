/**
  Problem statement:

  As part of the migration of legacy pathfinder into the new service model some flyway migration patches
  referenced legacy pathfinder tables. Now these legacy tables have are to be dropped you cannot start pathfinder from scratch
  due to the required tables no longer existing.
*/

/*
  This manual patch removes any patch entries that are to do with migration* now those migration files have been
  removed from resources/db/migration. The code that was executed has been moved to /manual-scripts/migration as a
  record of what was executed on the database during the migration.

  *The only exception being patch 5.3 which contains a number of grants relating to the actual pathfinder service but
  one which is to do with migration. 5.3 is being removed so when the service is deployed with the run out of order flag
  set the patch will be run, the grants re-executed and the correct flyway history record added. Without the removal of this
  patch number the checksum would fail as the file contents has technically changed.
*/
DELETE FROM pathfinder_flyway."flyway_schema_history" fsh
WHERE fsh."version" IN (
  3.9
, 4.0
, 4.3
, 4.5
, 4.6
, 4.7
, 4.8
, 5.1
, 5.2
, 5.3
, 6.0
, 7.5
, 7.7
, 7.8
, 7.9
);

/*
  This manual patch removes all re-runnable flyway patches from the migration history so that we don't have references
  to re-runnable patches that no longer exist in the code source.

  The code that was executed has been moved to /manual-scripts/migration as a record of what was executed on the database during the migration.
*/
DELETE FROM pathfinder_flyway."flyway_schema_history" fsh
WHERE fsh."version" IS NULL;

COMMIT;