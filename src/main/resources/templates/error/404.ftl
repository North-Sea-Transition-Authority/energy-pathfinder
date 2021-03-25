<#include '../layout.ftl'>

<@defaultPage
  htmlTitle="Page not found"
  pageHeading="Page not found"
  topNavigation=false
  twoThirdsColumn=true
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