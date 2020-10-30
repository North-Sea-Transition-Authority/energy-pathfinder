ALTER TABLE ${datasource.user}.project_details
ADD submitted_datetime TIMESTAMP;

ALTER TABLE ${datasource.user}.project_details
ADD submitted_by_wua NUMBER;
