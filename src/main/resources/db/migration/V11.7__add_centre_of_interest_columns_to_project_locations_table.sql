ALTER TABLE ${datasource.user}.project_locations
ADD (
  centre_of_interest_latitude_degrees INTEGER
, centre_of_interest_latitude_minutes INTEGER
, centre_of_interest_latitude_seconds NUMBER
, centre_of_interest_latitude_hemisphere VARCHAR2(4000)
, centre_of_interest_longitude_degrees INTEGER
, centre_of_interest_longitude_minutes INTEGER
, centre_of_interest_longitude_seconds NUMBER
, centre_of_interest_longitude_hemisphere VARCHAR2(4000)
);
