GRANT SELECT ON decmgr.xview_organisation_units TO ${datasource.user};
GRANT SELECT ON decmgr.current_org_grp_organisations TO ${datasource.user};
GRANT SELECT ON decmgr.current_organisation_groups TO ${datasource.user};
GRANT SELECT ON decmgr.organisation_address_details TO ${datasource.user};

GRANT SELECT ON decmgr.xview_resources TO ${datasource.user};
GRANT SELECT ON decmgr.xview_resource_types TO ${datasource.user};
GRANT SELECT ON decmgr.xview_resource_type_roles TO ${datasource.user};
GRANT SELECT ON decmgr.xview_resource_type_privs TO ${datasource.user};
GRANT SELECT ON decmgr.resource_usages_current TO ${datasource.user};
GRANT SELECT ON decmgr.resource_members_current TO ${datasource.user};
GRANT SELECT ON decmgr.xview_resource_people_history TO ${datasource.user};

GRANT SELECT ON decmgr.resources TO ${datasource.user};
GRANT SELECT ON decmgr.resource_people TO ${datasource.user};
GRANT EXECUTE ON decmgr.contact TO ${datasource.user};

GRANT SELECT ON devukmgr.field_operator_view TO ${datasource.user};
GRANT REFERENCES ON devukmgr.field_operator_view TO ${datasource.user};
GRANT SELECT ON devukmgr.fields TO ${datasource.user};
GRANT REFERENCES ON devukmgr.fields TO ${datasource.user};

GRANT SELECT ON pedmgr.ped_licence_details TO ${datasource.user};
GRANT SELECT ON pedmgr.ped_licences TO ${datasource.user};
GRANT SELECT ON pedmgr.ped_current_data_points TO ${datasource.user};
GRANT SELECT ON pedmgr.ped_current_licence_blocks TO ${datasource.user};
GRANT SELECT ON pedmgr.xview_ped_ld_current TO ${datasource.user};
GRANT EXECUTE ON pedmgr.ped_utils TO ${datasource.user};

GRANT SELECT ON devukmgr.facilities TO ${datasource.user};

GRANT SELECT ON securemgr.web_user_accounts TO ${datasource.user};

GRANT SELECT ON securemgr.web_user_sessions TO ${datasource.user};