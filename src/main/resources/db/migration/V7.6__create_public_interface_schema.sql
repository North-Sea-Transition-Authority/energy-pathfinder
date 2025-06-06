CREATE USER ${datasource.public-interface-user} IDENTIFIED BY "${datasource.public-interface-password}"
DEFAULT TABLESPACE tbsdata
TEMPORARY TABLESPACE TEMP
PROFILE mgr_user
QUOTA UNLIMITED ON TBSBLOB
QUOTA UNLIMITED ON TBSCLOB
QUOTA UNLIMITED ON TBSDATA
QUOTA UNLIMITED ON TBSIDX;

GRANT CREATE SESSION TO ${datasource.public-interface-user};
