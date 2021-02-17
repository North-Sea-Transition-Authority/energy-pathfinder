<#include '../layout.ftl'>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle topNavigation=true twoThirdsColumn=true breadcrumbs=true>
  <@fdsForm.htmlForm springUrl(addCommunicationUrl)>
    <@fdsAction.button buttonText="Send new email" start=false buttonClass="govuk-button govuk-button--blue" />
  </@fdsForm.htmlForm>
</@defaultPage>