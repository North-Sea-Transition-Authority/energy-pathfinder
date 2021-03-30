GRANT SELECT ON devukmgr.api_non_deleted_fields TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON pedmgr.ped_licence_details TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON pedmgr.ped_licences TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON pedmgr.ped_current_data_points TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON pedmgr.ped_current_licence_blocks TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON pedmgr.xview_ped_ld_current TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON devukmgr.facilities TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON wellmgr.api_extant_wellbores TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON securemgr.web_user_accounts TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON securemgr.web_user_sessions TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON decmgr.xview_organisation_units TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON decmgr.current_org_grp_organisations TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON decmgr.current_organisation_groups TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON decmgr.organisation_address_details TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON decmgr.xview_resources TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON decmgr.xview_resource_types TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON decmgr.xview_resource_type_roles TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON decmgr.xview_resource_type_privs TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON decmgr.resource_usages_current TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON decmgr.resource_members_current TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON decmgr.xview_resource_people_history TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON decmgr.resources TO ${datasource.user} WITH GRANT OPTION;

GRANT SELECT ON decmgr.resource_people TO ${datasource.user} WITH GRANT OPTION;

REVOKE SELECT ON devukmgr.field_operator_view FROM ${datasource.user};