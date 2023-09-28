<#include '../layout.ftl'>

<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->

<#assign pageHeading = "Subscription preferences updated" />

<@defaultPage
  htmlTitle=pageHeading
  pageHeading=""
  breadcrumbs=false
  topNavigation=false
>
    <@fdsFlash.flash
      flashTitle="You have updated your subscription preferences for the ${service.serviceName} newsletter"
      flashClass="fds-flash--green"
    />

    <@fdsAction.link
      linkUrl=springUrl(backToManageUrl)
      linkText="Back to manage subscription"
      linkClass="govuk-link"
    />
</@defaultPage>
