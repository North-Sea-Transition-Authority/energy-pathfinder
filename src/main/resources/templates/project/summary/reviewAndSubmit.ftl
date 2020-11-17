<#include '../../layoutPane.ftl'>
<#import 'projectSummary.ftl' as projectSummary/>

<#assign pageHeading="Review and submit project"/>

<@projectSummary.summaryWithSubNavigation
  pageHeading=pageHeading
  projectSummaryView=projectSummaryView
  sidebarHeading="Check your answers for all sections on the project"
>
  <@fdsForm.htmlForm actionUrl=springUrl(submitProjectUrl)>
    <@fdsAction.button buttonText="Submit" buttonValue="submit" />
    <@fdsAction.link linkText="Back to task list" linkClass="govuk-link govuk-link--button" linkUrl=springUrl(taskListUrl)/>
  </@fdsForm.htmlForm>
</@projectSummary.summaryWithSubNavigation>
