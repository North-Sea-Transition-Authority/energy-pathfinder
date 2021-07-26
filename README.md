# Pathfinder

## Project setup

### Prerequisites
* Java 11
* Node LTS + NPM
* IntelliJ Ultimate

### Steps

#### Initialise the Fivium Design System
* `git submodule update --init --recursive`    
* `cd fivium-design-system-core && npm install && npx gulp build && cd ..`

#### Build frontend components
* `npm install`
* `npx gulp buildAll`

#### Configure the following environment variables


| Environment Variable | Description |
| -------------------- |-------------|
| DB_SCHEMA_NAME | Database schema to connect as. E.g. `PATHFINDER_XX` This schema will be created for you by Flyway|
| CONTEXT_SUFFIX | A unique per developer suffix string to apply to the application context path. E.g. your initials |
| PATHFINDER_GOVUK_NOTIFY_API_KEY | API key for the environment. for Local dev see TPM https://tpm.fivium.local/index.php/pwd/view/1569 |
| PATHFINDER_TEST_EMAIL_RECIPIENT | Email address to send all emails to when email.mode = "test" |


#### Create the Flyway user

This must be your DB_SCHEMA_NAME with '_flyway' appended to the end.


```oraclesqlplus
CREATE USER pathfinder_xx_flyway IDENTIFIED BY "<password>"
/

GRANT UNLIMITED TABLESPACE TO pathfinder_xx_flyway WITH ADMIN OPTION
/

GRANT
  CREATE SESSION, 
  CREATE USER,
  DROP ANY TABLE,
  CREATE ANY TABLE,
  CREATE TABLE, -- Not covered by above grant. They are different.
  CREATE ANY VIEW,
  CREATE ANY INDEX, 
  SELECT ANY TABLE,
  DELETE ANY TABLE,
  LOCK ANY TABLE,
  INSERT ANY TABLE, 
  UPDATE ANY TABLE,
  ALTER ANY TABLE,
  DROP ANY INDEX,
  CREATE ANY SEQUENCE,
  SELECT ANY SEQUENCE,
  CREATE ANY PROCEDURE,
  GRANT ANY OBJECT PRIVILEGE
TO pathfinder_xx_flyway WITH ADMIN OPTION
/ 

GRANT EXECUTE ON decmgr.contact TO pathfinder_xx_flyway
/
```
This user must be created before the app runs for the first time on a new DB. All migrations will be run by this flyway user.

#### Set the active profile
Set the profile to `development` in your run configuration

#### Setup Checkstyle
* Install the Checkstyle-IDEA plugin (from third-party repositories)
* Go to Settings > Checkstyle
* Add a "Configuration File"
* "Use a local Checkstyle file"
* Select `ide/checkstyle.xml`
* Check box for "Store relative to project location" 
* Check the "Active" box next to the new profile
  
  Note that Checkstyle rules are checked during the build process and any broken rules will fail the build.
    
#### Proxy routes to enable session sharing

To enable Spring to access the fox session, you must access your local instance under the same hostname and application context (itportal.dev.decc.local/engedudev1/).

The easiest way to do this is to add a ProxyPass rule to Apache running on itportal.dev.decc.local.
 
Edit the `nginx` configuration file at https://bitbucket.org/fiviumuk/oga-dev-app/src/master/app/volumes/nginx/nginx.conf and add a ProxyPass rule forwarding traffic under your CONTEXT_SUFFIX to your local machine e.g.

```
location /engedudev1/da/ {
  proxy_pass  http://dashworth.fivium.local:8081/engedudev1/da/;
}
```

Once you have added the ProxyPass rule you will need to increment the version number of the nginx configuration file in https://bitbucket.org/fiviumuk/oga-dev-app/src/master/app/compose/uni.yml. See [OGA Bloomsbury Street infrastructure](https://confluence.fivium.co.uk/pages/viewpage.action?pageId=67733766#EDU/MMO/ETLdev/stBloomsburyStreet(OGA)-HowdoIupdatetheconfigforanapp) for more infomration.

For example if the following was included in the `uni.yml` file

`source: nginx_nginx.conf1`

This will need to be updated to

`source: nginx_nginx.conf2`

There are two references to the nginx config version within this file so ensure you update both.

Commit and push the changes to both files and once the build has passed the ProxyPass rule will be ready to use.

#### Run the app
IntelliJ should auto detect the Spring application and create a run configuration.
Run the project and navigate to `http://edu-dev-app3.decc.local/engedudev1/CONTEXT_SUFFIX/test`
    