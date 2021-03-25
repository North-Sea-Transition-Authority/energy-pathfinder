CREATE TABLE ${datasource.user}.communication_recipients (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
, communication_id NUMBER NOT NULL
, sent_to_email_address VARCHAR2(4000) NOT NULL
, sent_datetime TIMESTAMP NOT NULL
, CONSTRAINT communication_recipients_fk FOREIGN KEY (communication_id) REFERENCES ${datasource.user}.communications (id)
) TABLESPACE tbsdata;

CREATE INDEX ${datasource.user}.communication_recipients_idx
ON ${datasource.user}.communication_recipients (communication_id)
TABLESPACE tbsidx;