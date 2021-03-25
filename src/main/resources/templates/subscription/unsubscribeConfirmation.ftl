<#include '../layout.ftl'>

<#assign pageHeading = "Unsubscribed" />

<@defaultPage htmlTitle=pageHeading pageHeading="" breadcrumbs=false topNavigation=false>
  <@fdsFlash.flash
    flashTitle="You have been unsubscribed from the ${service.serviceName} newsletter"
    flashClass="fds-flash--green"
  />

  <p class="govuk-body">
    You can <@fdsAction.link linkText="resubscribe to the ${service.serviceName} newsletter" linkUrl=springUrl(resubscribeUrl) /> if you've unsubscribed by accident.
  </p>
</@defaultPage>
