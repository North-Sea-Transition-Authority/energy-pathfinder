<#include '../layout.ftl'>

<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->

<#assign pageTitle = "Thank you for your feedback"/>

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  topNavigation=false
  backLink=false
  phaseBanner=false
>

  <p class="govuk-body">You can now close this tab to return to ${service.serviceName}</p>

</@defaultPage>