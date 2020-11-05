<#include '../../layoutPane.ftl'>
<#import 'projectSummary.ftl' as projectSummary/>

<#assign pageHeading="Review and submit project"/>
<@defaultPagePane htmlTitle=pageHeading phaseBanner=false>

  <@projectSummary.summary
    pageHeading=pageHeading
    projectSummaryView=projectSummaryView
    sidebarHeading="Check your answers for all sections on the project"
  >
    <@fdsForm.htmlForm actionUrl=springUrl(submitProjectUrl)>
      <@fdsAction.button buttonText="Submit" buttonValue="submit" />
      <@fdsAction.link linkText="Back to task list" linkClass="govuk-link govuk-link--button" linkUrl=springUrl(taskListUrl)/>
    </@fdsForm.htmlForm>
  </@projectSummary.summary>

</@defaultPagePane>