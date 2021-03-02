<#include '../layout.ftl'>

<#assign pageHeading = "Are you sure you want to unsubscribe from the ${service.serviceName} newsletter?">

<@defaultPage htmlTitle=pageHeading pageHeading=pageHeading fullWidthColumn=true breadcrumbs=false topNavigation=false>
  <p class="govuk-body">You will no longer receive an email once a month showing new or updated ${service.serviceName} projects.</p>
  <@fdsForm.htmlForm>
    <@fdsAction.button buttonText="Unsubscribe" />
  </@fdsForm.htmlForm>
</@defaultPage>
