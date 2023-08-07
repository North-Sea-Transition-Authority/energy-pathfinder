<#include '../layout.ftl'>

<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->

<#assign serviceName = service.serviceName>
<#assign pageHeading="Manage your ${serviceName} subscription">

<@defaultPage
  htmlTitle=pageHeading
  pageHeading=pageHeading
  breadcrumbs=false
  topNavigation=false
>
  <@fdsForm.htmlForm>
    <p class="govuk-body">
      Subscription for <b>${subscriberEmail}</b>
    </p>

      <@fdsRadio.radio
        path="form.subscriptionManagementOption"
        radioItems=managementOptions
        labelText="What would you like to change about your subscription?"
      />

      <@fdsAction.button buttonText="Continue" />
  </@fdsForm.htmlForm>

</@defaultPage>
