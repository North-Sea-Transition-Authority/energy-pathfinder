<#include '../layout.ftl'>
<#include 'errorReference.ftl'>

<#-- @ftlvariable name="errorRef" type="String" -->
<#-- @ftlvariable name="technicalSupportContact" type="uk.co.ogauthority.pathfinder.model.enums.contact.ServiceContactDetail" -->
<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->

<#assign pageTitle = "Sorry, there is a problem with the service" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  topNavigation=false
  twoThirdsColumn=true
  phaseBanner=false
>
  <p class="govuk-body">Try again later.</p>
  <p class="govuk-body">
    If you continue to experience this problem, contact the service desk using the
    details below. Be sure to include the error reference below in any correspondence.
  </p>
  <@errorReference reference=errorRef!>
    <ul class="govuk-list">
      <li>${technicalSupportContact.serviceName}</li>
      <li>${technicalSupportContact.phoneNumber}</li>
      <li>
        <@fdsAction.link
          linkText=technicalSupportContact.emailAddress
          linkUrl="mailto:${technicalSupportContact.emailAddress}?subject=${service.serviceName} - Error reference ${errorRef}"
        />
      </li>
    </ul>
  </@errorReference>
</@defaultPage>