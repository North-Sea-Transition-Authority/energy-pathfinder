<#include '../layout.ftl'>

<#assign pageHeading = "Start project">

<@defaultPage htmlTitle=pageHeading pageHeading=pageHeading backLink=true>

    <@fdsStartPage.startPage startActionText="Start project" startActionUrl=buttonUrl>

      <p class="govuk-body">Pathfinder has been established to provide a real time look at energy projects in the UKCS.</p>

      <p class="govuk-body">Create a project to provide information about it's location, project type, timings as well as contact details within the company.</p>

    </@fdsStartPage.startPage>

</@defaultPage>