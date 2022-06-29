<#include '../layout.ftl'>

<#assign pageHeading = "Already unsubscribed" />

<@defaultPage htmlTitle=pageHeading pageHeading="" breadcrumbs=false topNavigation=false>
    <@fdsFlash.flash flashTitle="You are already unsubscribed from the ${service.serviceName} newsletter"/>

  <p class="govuk-body">
    You can <@fdsAction.link linkText="resubscribe to the ${service.serviceName} newsletter" linkUrl=springUrl(resubscribeUrl) /> if you meant to subscribe.
  </p>
</@defaultPage>
