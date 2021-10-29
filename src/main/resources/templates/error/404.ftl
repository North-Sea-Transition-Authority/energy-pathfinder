<#include '../layout.ftl'>

<#-- @ftlvariable name="technicalSupportContact" type="uk.co.ogauthority.pathfinder.model.enums.contact.ServiceContactDetail" -->
<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->

<#assign pageTitle = "Page not found" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  topNavigation=false
  twoThirdsColumn=true
  phaseBanner=false
>
  <p class="govuk-body">
    If you typed the web address, check it is correct.
  </p>
  <p class="govuk-body">
    If you pasted the web address, check you copied the entire address.
  </p>
  <p class="govuk-body">
    If the web address is correct or you selected a link or button, contact the service desk using the details below:
  </p>
  <ul class="govuk-list">
    <li>${technicalSupportContact.serviceName}</li>
    <li>${technicalSupportContact.phoneNumber}</li>
    <li>
        <@fdsAction.link
        linkText=technicalSupportContact.emailAddress
        linkUrl="mailto:${technicalSupportContact.emailAddress}?subject=${service.serviceName} - Page Not Found"
        />
    </li>
  </ul>
</@defaultPage>