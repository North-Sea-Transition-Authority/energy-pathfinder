<#include '../layout.ftl'>

<#assign pageHeading = "Subscribed" />

<@defaultPage htmlTitle=pageHeading pageHeading="" breadcrumbs=false topNavigation=false>
  <@fdsFlash.flash
    flashTitle="You have been subscribed to the ${service.serviceName} newsletter"
    flashClass="fds-flash--green"
  >
    <p class="govuk-body">You'll receive an email once a month showing new or updated ${service.serviceName} projects</p>
  </@fdsFlash.flash>
</@defaultPage>
