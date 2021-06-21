<#include '../../../layout.ftl'>

<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->
<#-- @ftlvariable name="infrastructureProjectTypeLowercaseDisplayName" type="String" -->

<#assign pageHeading = "Start ${infrastructureProjectTypeLowercaseDisplayName}">

<@defaultPage htmlTitle=pageHeading pageHeading=pageHeading backLink=true twoThirdsColumn=true>
  <@fdsStartPage.startPage startActionText=pageHeading startActionUrl=startActionUrl>
    <p class="govuk-body">
      ${service.serviceName} has been established to provide a real time look at energy
      ${infrastructureProjectTypeLowercaseDisplayName}s in the UKCS.
    </p>
    <p class="govuk-body">
      Create a ${infrastructureProjectTypeLowercaseDisplayName} to provide information about its location,
      ${infrastructureProjectTypeLowercaseDisplayName} type, timings and relevant contact details.
    </p>
  </@fdsStartPage.startPage>
</@defaultPage>