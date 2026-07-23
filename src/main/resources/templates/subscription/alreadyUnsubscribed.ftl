<#include '../layout.ftl'>

<#assign pageHeading = "Already unsubscribed" />

<@defaultPage htmlTitle=pageHeading pageHeading="" breadcrumbs=false topNavigation=false>
  <@fdsFlash.flash flashTitle="You are not subscribed to the ${service.serviceName} newsletter"/>

  <p class="govuk-body">
    <@fdsAction.link linkText="Subscribe to the ${service.serviceName} newsletter" linkUrl=springUrl(resubscribeUrl) />
  </p>
</@defaultPage>
