GRANT SELECT ON decmgr.path_projects TO pathfinder_migration;

GRANT SELECT ON decmgr.path_project_details TO pathfinder_migration;

GRANT SELECT, INSERT ON pathfinder.projects TO pathfinder_migration;

GRANT SELECT, INSERT ON pathfinder.project_details TO pathfinder_migration;

GRANT SELECT, INSERT ON pathfinder.project_publishing_details TO pathfinder_migration;

GRANT SELECT ON decmgr.xview_resource_people_history TO pathfinder_migration;

GRANT SELECT ON decmgr.newsletter_recipients TO pathfinder_migration;

GRANT SELECT, INSERT ON pathfinder.subscribers TO pathfinder_migration;

GRANT SELECT ON decmgr.path_operators TO pathfinder_migration;

GRANT SELECT ON securemgr.web_user_accounts TO pathfinder_migration;

GRANT SELECT ON decmgr.resource_members_current TO pathfinder_migration;

GRANT EXECUTE ON decmgr.contact TO pathfinder_migration;

GRANT SELECT ON decmgr.resources TO pathfinder_migration;

GRANT SELECT ON decmgr.current_organisation_groups TO pathfinder_migration;

GRANT SELECT ON decmgr.resource_usages_current TO pathfinder_migration;

GRANT SELECT ON decmgr.xview_resources TO pathfinder_migration;

GRANT SELECT, INSERT ON pathfinder.project_operators TO pathfinder_migration;

GRANT SELECT, INSERT ON pathfinder.project_information TO pathfinder_migration;

GRANT SELECT, INSERT ON pathfinder.project_locations TO pathfinder_migration;

GRANT SELECT ON devukmgr.fields TO pathfinder_migration;

GRANT SELECT, INSERT ON pathfinder.awarded_contracts TO pathfinder_migration;

GRANT SELECT, INSERT ON pathfinder.collaboration_opportunities TO pathfinder_migration;

GRANT SELECT, INSERT ON pathfinder.project_task_list_setup TO pathfinder_migration;

GRANT SELECT, INSERT ON pathfinder.project_location_blocks TO pathfinder_migration;

GRANT EXECUTE ON pedmgr.ped_utils TO pathfinder_migration;

GRANT SELECT, INSERT ON pathfinder.project_updates TO pathfinder_migration;

GRANT SELECT, INSERT ON pathfinder.project_archive_details TO pathfinder_migration;

GRANT SELECT, INSERT ON pathfinder.decommissioning_schedules TO pathfinder_migration;