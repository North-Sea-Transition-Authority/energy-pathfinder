ALTER TABLE ${datasource.user}.awarded_contracts
RENAME COLUMN project_details_id TO project_detail_id;

ALTER TABLE ${datasource.user}.collaboration_opportunities
RENAME COLUMN project_details_id TO project_detail_id;

ALTER TABLE ${datasource.user}.project_locations
RENAME COLUMN project_details_id TO project_detail_id;

ALTER TABLE ${datasource.user}.project_information
RENAME COLUMN project_details_id TO project_detail_id;

ALTER TABLE ${datasource.user}.upcoming_tenders
RENAME COLUMN project_details_id TO project_detail_id;

ALTER TABLE ${datasource.user}.project_operators
RENAME COLUMN project_details_id TO project_detail_id;