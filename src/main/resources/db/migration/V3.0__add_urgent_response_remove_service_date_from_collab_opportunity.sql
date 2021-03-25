ALTER TABLE ${datasource.user}.collaboration_opportunities DROP COLUMN estimated_service_date;

ALTER TABLE ${datasource.user}.collaboration_opportunities ADD urgent_response_needed NUMBER;