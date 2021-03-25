CREATE TABLE ${datasource.user}.subscribers (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, uuid VARCHAR2(36) NOT NULL
, forename VARCHAR2(4000) NOT NULL
, surname VARCHAR2(4000) NOT NULL
, email_address VARCHAR2(4000) NOT NULL
, relation_to_pathfinder VARCHAR2(4000) NOT NULL
, subscribe_reason CLOB
, subscribed_datetime TIMESTAMP NOT NULL
, CONSTRAINT uuid_unique UNIQUE (uuid)
, CONSTRAINT email_address_unique UNIQUE (email_address)
) TABLESPACE tbsdata;
