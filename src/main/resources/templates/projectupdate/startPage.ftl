<#include '../layout.ftl'>

<#assign pageHeading = "Start update">

<@defaultPage htmlTitle=pageHeading pageHeading=pageHeading backLink=true twoThirdsColumn=true>
  <@fdsStartPage.startPage startActionText=pageHeading startActionUrl=startActionUrl>
    <p class="govuk-body">
      You should update your ${projectTypeDisplayNameLowercase}, or advise if no updates are required, once a quarter.
      This helps us to ensure the information provided to the supply chain is up to date.
    </p>
  </@fdsStartPage.startPage>
</@defaultPage>
