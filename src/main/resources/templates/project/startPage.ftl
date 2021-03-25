<#include '../layout.ftl'>

<#assign pageHeading = "Start project">

<@defaultPage htmlTitle=pageHeading pageHeading=pageHeading backLink=true twoThirdsColumn=true>

    <@fdsStartPage.startPage startActionText=pageHeading startActionUrl=startActionUrl>

      <p class="govuk-body">${service.serviceName} has been established to provide a real time look at energy projects in the UKCS.</p>

      <p class="govuk-body">Create a project to provide information about its location, project type, timings and relevant contact details.</p>

    </@fdsStartPage.startPage>

</@defaultPage>