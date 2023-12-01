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

##### Development profile (`development`)

| Environment Variable                   | Description                                                                                                                                       |
|----------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| DB_SCHEMA_NAME                         | Database schema to connect as. E.g. `PATHFINDER_XX` This schema will be created for you by Flyway                                                 |
| CONTEXT_SUFFIX                         | A unique per developer suffix string to apply to the application context path. E.g. your initials                                                 |
| PATHFINDER_GOVUK_NOTIFY_API_KEY        | API key for the environment. For local dev see [TPM](https://tpm.fivium.co.uk/index.php/pwd/view/1569)                                            |
| PATHFINDER_TEST_EMAIL_RECIPIENT        | Email address to send all emails to when email.mode = "test"                                                                                      |
| PATHFINDER_ANALYTICS_APP_API_SECRET    | API key to allow posting data to analytics endpoint for app metrics. For local dev see [TPM](https://tpm.fivium.co.uk/index.php/pwd/view/1856)    |
| PATHFINDER_ANALYTICS_GLOBAL_API_SECRET | API key to allow posting data to analytics endpoint for global metrics. For local dev see [TPM](https://tpm.fivium.co.uk/index.php/pwd/view/1865) |

##### Production profile (`production`)

| Environment Variable                    | Description                                                                                                                                                                                                                                                                                                            |
|-----------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| PATHFINDER_DB_URL                       | The URL to the database you want to use                                                                                                                                                                                                                                                                                |
| PATHFINDER_DB_SCHEMA                    | Database schema to connect as. E.g. `PATHFINDER_XX` This schema will be created for you by Flyway                                                                                                                                                                                                                      |
| PATHFINDER_DB_PASSWORD                  | The password to the `PATHFINDER_DA_SCHEMA` schema                                                                                                                                                                                                                                                                      |
| PATHFINDER_CONTEXT                      | A unique per developer suffix string to apply to the application context path. E.g. your initials                                                                                                                                                                                                                      |
| PATHFINDER_GOVUK_NOTIFY_API_KEY         | API key for the environment. for Local dev see TPM https://tpm.fivium.local/index.php/pwd/view/1569                                                                                                                                                                                                                    |
| PATHFINDER_TEST_EMAIL_RECIPIENT         | Email address to send all emails to when email.mode = "test"                                                                                                                                                                                                                                                           |
| PATHFINDER_BASE_URL                     | The non Pathfinder specific URL prefix e.g. [https://itportal.dev.decc.local](https://itportal.dev.decc.local)                                                                                                                                                                                                         |
| PATHFINDER_FOX_LOGIN_URL                | The login URL for the Energy Portal on the environment you want to use, e.g [https://itportal.dev.decc.local/engedudev1/fox/nsta/NSTA_LOGIN/login?REFERRED_BY=PATHFINDER&DEV_CONTEXT_OVERRIDE=xx](https://itportal.dev.decc.local/engedudev1/fox/nsta/NSTA_LOGIN/login?REFERRED_BY=PATHFINDER&DEV_CONTEXT_OVERRIDE=xx) |
| PATHFINDER_FOX_LOGOUT_URL               | The logout URL for the Energy Portal on the environment you want to use, e.g [https://itportal.dev.decc.local/engedudev1/fox/nsta/NSTA_LOGIN/logout](https://itportal.dev.decc.local/engedudev1/fox/nsta/NSTA_LOGIN/logout)                                                                                            |
| PATHFINDER_FOX_REGISTRATION_URL         | The registration URL for the Energy Portal on the environment you want you use, e.g [https://itportal.dev.decc.local/engedudev1/fox?foxopen=nsta/LOGIN001L/register](https://itportal.dev.decc.local/engedudev1/fox?foxopen=nsta/LOGIN001L/register)                                                                   |
| PATHFINDER_CLAMAV_HOST                  | The host URL for ClamAV antivirus                                                                                                                                                                                                                                                                                      |
| PATHFINDER_CLAMAV_PORT                  | The port for the ClamAV antivirus                                                                                                                                                                                                                                                                                      |
| PATHFINDER_CLAMAV_TIMEOUT               | The timeout for the ClamAV antivirus                                                                                                                                                                                                                                                                                   |
| REGULATOR_SHARED_EMAIL                  | An email address to send to when an email is sent to the regulator shared inbox                                                                                                                                                                                                                                        |
| PATHFINDER_PUBLIC_INTERFACE_DB_PASSWORD | The password to create the `PATHFINDER_INTERFACE[_XX]` schema with                                                                                                                                                                                                                                                     |
| PATHFINDER_SUPPLY_CHAIN_INTERFACE_URL   | The URL to the supply chain interface (for local dev this can be any URL)                                                                                                                                                                                                                                              |
| PATHFINDER_ENABLE_STATSD                | Boolean paramter to enable statsd stat aggregation                                                                                                                                                                                                                                                                     |
| PATHFINDER_ENABLE_FLYWAY_OUT_OF_ORDER   | Boolean paramater to allow flyway to run migrations out of order                                                                                                                                                                                                                                                       |
| PATHFINDER_ANALYTICS_ENABLED            | Whether or not analytics functionality is enabled                                                                                                                                                                                                                                                                      |
| PATHFINDER_ANALYTICS_ENDPOINT_URL       | Endpoint for analytics collection                                                                                                                                                                                                                                                                                      |
| PATHFINDER_ANALYTICS_USER_AGENT         | Identification of host posting analytics data                                                                                                                                                                                                                                                                          |
| PATHFINDER_ANALYTICS_CONN_TIMEOUT_SECS  | Timeout to be used when trying to connect to analytics endpoints                                                                                                                                                                                                                                                       |
| PATHFINDER_ANALYTICS_APP_TAG            | App-specific measurement id to separate analytics collection                                                                                                                                                                                                                                                           |
| PATHFINDER_ANALYTICS_GLOBAL_TAG         | Portal-wide measurement id to separate analytics collection                                                                                                                                                                                                                                                            |
| PATHFINDER_ANALYTICS_APP_API_SECRET     | API key to allow posting data to analytics endpoint for app metrics                                                                                                                                                                                                                                                    |
| PATHFINDER_ANALYTICS_GLOBAL_API_SECRET  | API key to allow posting data to analytics endpoint for global metrics                                                                                                                                                                                                                                                 |
| PATHFINDER_API_PRE_SHARED_KEY           | The API key used to validate requests from the Energy Portal API                                                                                                                                                                                                                                                       |

##### Debug profile (`debug`)
Add the debug profile to enable hibernate SQL and descriptor output. You can look in the `application-debug.properties` file to enable other debug properties if requried.

No environment variables need to be added when enabling the debug profile.

#### Create the Flyway user

See the [environments] (https://fivium.atlassian.net/wiki/spaces/PAT/pages/13402153/Environments) page to find the development database.

This must be your DB_SCHEMA_NAME with '_flyway' appended to the end.

Run the script below as the `xviewmgr` user


```oraclesqlplus
CREATE USER pathfinder_xx_flyway IDENTIFIED BY "dev1"
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

To enable Spring to access the fox session, you must access your local instance under the same hostname and application context (itportal.dev.fivium.local/engedudev1/).

The easiest way to do this is to add a ProxyPass rule to Apache running on itportal.dev.fivium.local. You only need to do this if you have never done this before for any Energy Portal spring services
 
Edit the [nginx configuration file](https://bitbucket.org/fiviumuk/fivium-dev-app/src/master/app/volumes/ops/nginx_oga/nginx.conf) at and add a ProxyPass rule forwarding traffic under your CONTEXT_SUFFIX to your local machine under the `itportal.dev.fivium.local` server. An example is shown below e.g.

```
location /engedudev1/da/ {
  proxy_pass  http://dashworth.fivium.local:8081/engedudev1/da/;
}
```

Once you have added the ProxyPass rule you will need to increment the version number of the nginx configuration file in the [ops.yml](https://bitbucket.org/fiviumuk/fivium-dev-app/src/master/app/compose/ops.yml) file. See [OGA Bloomsbury Street infrastructure](https://confluence.fivium.co.uk/pages/viewpage.action?pageId=67733766#EDU/MMO/ETLdev/stBloomsburyStreet(OGA)-HowdoIupdatetheconfigforanapp) for more infomration.

For example if the following was included in the `ops.yml` file

`source: nginx_nginx.conf1`

This will need to be updated to

`source: nginx_nginx.conf2`

There are two references to the nginx config version within this file so ensure you update both.

Commit and push the changes to both files and once the build has passed the ProxyPass rule will be ready to use.

#### Run the app
IntelliJ should auto detect the Spring application and create a run configuration.
Run the project and navigate to `https://itportal.dev.fivium.local/engedudev1/<CONTEXT_SUFFIX>/work-area`
