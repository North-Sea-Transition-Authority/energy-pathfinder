# Pathfinder

## Project setup

### Prerequisites
* Java 21
* Node LTS + NPM
* IntelliJ Ultimate
* Docker

### Steps

#### Initialise the Fivium Design System
* `git submodule update --init --recursive`    
* `cd fivium-design-system-core && npm install && npx gulp buildAll && cd ..`

#### Build frontend components
* `npm install`
* `npx gulp buildAll`

#### Configure the following environment variables

##### Regardless of profile

| Environment Variable                             | Description                                                                                                                                                                                                                                                                                               |
|--------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| PATHFINDER_EPMQ_SNS_SQS_AWS_ACCESS_KEY_ID        | AWS access key id for SNS/SQS. For local: https://tpm.fivium.co.uk/index.php/pwd/view/2134                                                                                                                                                                                                                |
| PATHFINDER_EPMQ_SNS_SQS_AWS_SECRET_ACCESS_KEY    | AWS secret access key for SNS/SQS. For local: https://tpm.fivium.co.uk/index.php/pwd/view/2134                                                                                                                                                                                                            |
| PATHFINDER_EPMQ_SNS_SQS_AWS_REGION_ID (optional) | The AWS region to run in. Defaults to `eu-west-2`                                                                                                                                                                                                                                                         |
| PATHFINDER_EPMQ_ENVIRONMENT_SUFFIX               | Something unique per environment, e.g. `dev`. For local dev this can be your initials.                                                                                                                                                                                                                    |

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

| Environment Variable                        | Description                                                                                                    |
|---------------------------------------------|----------------------------------------------------------------------------------------------------------------|
| PATHFINDER_DB_URL                           | The URL to the database you want to use                                                                        |
| PATHFINDER_DB_SCHEMA                        | Database schema to connect as. E.g. `PATHFINDER_XX` This schema will be created for you by Flyway              |
| PATHFINDER_DB_PASSWORD                      | The password to the `PATHFINDER_DA_SCHEMA` schema                                                              |
| PATHFINDER_CONTEXT                          | A unique per developer suffix string to apply to the application context path. E.g. your initials              |
| PATHFINDER_GOVUK_NOTIFY_API_KEY             | API key for the environment. for Local dev see TPM https://tpm.fivium.local/index.php/pwd/view/1569            |
| PATHFINDER_TEST_EMAIL_RECIPIENT             | Email address to send all emails to when email.mode = "test"                                                   |
| PATHFINDER_BASE_URL                         | The non Pathfinder specific URL prefix e.g. [https://itportal.dev.decc.local](https://itportal.dev.decc.local) |
| PATHFINDER_CLAMAV_HOST                      | The host URL for ClamAV antivirus                                                                              |
| PATHFINDER_CLAMAV_PORT                      | The port for the ClamAV antivirus                                                                              |
| PATHFINDER_CLAMAV_TIMEOUT                   | The timeout for the ClamAV antivirus                                                                           |
| REGULATOR_SHARED_EMAIL                      | An email address to send to when an email is sent to the regulator shared inbox                                |
| PATHFINDER_PUBLIC_INTERFACE_DB_PASSWORD     | The password to create the `PATHFINDER_INTERFACE[_XX]` schema with                                             |
| PATHFINDER_SUPPLY_CHAIN_INTERFACE_URL       | The URL to the supply chain interface (for local dev this can be any URL)                                      |
| PATHFINDER_ENABLE_STATSD                    | Boolean parameter to enable statsd stat aggregation                                                            |
| PATHFINDER_ENABLE_FLYWAY_OUT_OF_ORDER       | Boolean parameter to allow flyway to run migrations out of order                                               |
| PATHFINDER_ANALYTICS_ENABLED                | Whether or not analytics functionality is enabled                                                              |
| PATHFINDER_ANALYTICS_ENDPOINT_URL           | Endpoint for analytics collection                                                                              |
| PATHFINDER_ANALYTICS_USER_AGENT             | Identification of host posting analytics data                                                                  |
| PATHFINDER_ANALYTICS_CONN_TIMEOUT_SECS      | Timeout to be used when trying to connect to analytics endpoints                                               |
| PATHFINDER_ANALYTICS_APP_TAG                | App-specific measurement id to separate analytics collection                                                   |
| PATHFINDER_ANALYTICS_GLOBAL_TAG             | Portal-wide measurement id to separate analytics collection                                                    |
| PATHFINDER_ANALYTICS_APP_API_SECRET         | API key to allow posting data to analytics endpoint for app metrics                                            |
| PATHFINDER_ANALYTICS_GLOBAL_API_SECRET      | API key to allow posting data to analytics endpoint for global metrics                                         |
| PATHFINDER_API_PRE_SHARED_KEY               | The API key used to validate requests from the Energy Portal API                                               |
| PATHFINDER_PUBLIC_DATA_S3_ACCESS_KEY_ID     | AWS access key id for uploading to the public data S3 bucket                                                   |
| PATHFINDER_PUBLIC_DATA_S3_SECRET_ACCESS_KEY | AWS secret access key for uploading to the public data S3 bucket                                               |
| PATHFINDER_PUBLIC_DATA_S3_BUCKET            | S3 bucket to upload public data into                                                                           |
| PATHFINDER_GOOGLE_RECAPTCHA_SITE_KEY        | [Google reCAPTCHA Site Key](https://tpm.fivium.co.uk/index.php/pwd/view/2642)                                  |
| PATHFINDER_GOOGLE_RECAPTCHA_SECRET_KEY      | [Google reCAPTCHA Secret Key](https://tpm.fivium.co.uk/index.php/pwd/view/2642)                                |                                                                                                                                                                                                                          |
| EPAS_LOGIN_URL                              | The url to the `EPAS_REDIRECT` Fox module                                                                      |
| EPAS_LOGOUT_URL                             | The logout url of the new IDP e.g https://nsta.itportal.dev.fivium.co.uk/accounts/service-provider-sign-out    |
| EPAS_REGISTRATION_URL                       | The registration URL of the new IDP e.g. https://nsta.itportal.dev.fivium.co.uk/accounts/register              |                                                                                                                                                                                                                          |
| EPAS_SPRING_BOOT_STARTER_PRESHARED_KEY      | The preshared key that EPAS will use when calling out to this service                                          |

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
    
#### Run local fox engine to enable session sharing
To enable Spring to access the fox session, you must run a local fox instance on your machine. To do this run the compose file provided in `/devtools-pathfinder/local-dev-compose.yml`.

This will start a fox4 instance listening on `localhost:8080`.

See https://fivium.atlassian.net/wiki/spaces/JAVA/pages/15368483/Java+development+environment+setup#Javadevelopmentenvironmentsetup-Docker if you don't have Docker setup, or don't have the `repo1.dev.fivium.local` registry marked as allowing insecure connections.

#### Run the app
IntelliJ should auto detect the Spring application and create a run configuration.
Run the project and navigate to `http://localhost:8081/engedudev1/<CONTEXT_SUFFIX>/work-area`

You will be redirected to your local fox instance for authentication, and then redirected back to your local Pathfinder instance with an active session.

## Logging

Pathfinder can log in either JSON or text mode.

In order to turn on JSON logging, set the profile 'json-logging'. This will automatically include any MDC attributes.

## Page not loading

Accessing `http://localhost:8081/engedudev1/<CONTEXT_SUFFIX>/work-area` sometimes results in the page infinitely loading.

This is normally because the docker network ips are overlapping with the oracle dev database.

You should stop the pathfinder app and remove the pathfinder containers from your docker desktop before fixing.

We can fix this by getting the ip of the oracle database with and checking it against ips on our local docker network with and removing any that overlap by using.

Fix:

    - "docker network inspect `docker network ls -q` | grep -C 20 172.25"
    - identify the "Name" of the network
    - "docker network rm <name>"
