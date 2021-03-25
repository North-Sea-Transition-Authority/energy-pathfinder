ALTER TABLE ${datasource.user}.communications
ADD (
  greeting_text VARCHAR2(4000)
, sign_off_text VARCHAR2(4000)
, sign_off_identifier VARCHAR2(4000)
);

UPDATE ${datasource.user}.communications c
SET
  c.greeting_text = 'Dear'
, c.sign_off_text = 'Kind regards'
, c.sign_off_identifier = 'OGA pathfinder team';

ALTER TABLE ${datasource.user}.communications
MODIFY (
  greeting_text VARCHAR2(4000) NOT NULL
, sign_off_text VARCHAR2(4000) NOT NULL
, sign_off_identifier VARCHAR2(4000) NOT NULL
);