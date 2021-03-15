ALTER TABLE ${datasource.user}.project_details
ADD project_type VARCHAR2(4000);

UPDATE ${datasource.user}.project_details pd
SET pd.project_type = 'INFRASTRUCTURE';

ALTER TABLE ${datasource.user}.project_details
MODIFY project_type VARCHAR2(4000) NOT NULL;