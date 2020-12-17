<#include '../../layoutPane.ftl'>
<#import 'projectSummary.ftl' as projectSummary/>

<#if isUpdate>
  <#assign pageHeading="Review and submit project update" />
<#else>
  <#assign pageHeading="Review and submit project" />
</#if>

<@projectSummary.summaryWithSubNavigation
  pageHeading=pageHeading
  projectSummaryView=projectSummaryView
  sidebarHeading="Check your answers for all sections on the project"
  errorMessage=errorMessage
>
  <@fdsForm.htmlForm actionUrl=springUrl(submitProjectUrl)>
    <@fdsAction.button buttonText="Submit" buttonValue="submit" />
    <@fdsAction.link linkText="Back to task list" linkClass="govuk-link govuk-link--button" linkUrl=springUrl(taskListUrl)/>
  </@fdsForm.htmlForm>
</@projectSummary.summaryWithSubNavigation>
