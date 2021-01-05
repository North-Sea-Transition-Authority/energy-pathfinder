<#include '../../layoutPane.ftl'>
<#import 'projectSummary.ftl' as projectSummary/>

<#if isUpdate>
  <#assign pageHeading="Review and submit project update" />
<#else>
  <#assign pageHeading="Review and submit project" />
</#if>

<@defaultPageWithSidebar.defaultPageWithSidebar
  pageHeading=pageHeading
  themeHeading="Check your answers for all sections on the project"
  sidebarSectionLinks=projectSummaryView.sidebarSectionLinks>

  <#if !isProjectValid>
    <@_invalidProjectInset />
  </#if>

  <#if isUpdate>
    <@differenceChanges.toggler/>
  </#if>
  <@projectSummary.summary projectSummaryView=projectSummaryView />

  <#if !isProjectValid>
    <@_invalidProjectInset />
  <#else>
    <@fdsForm.htmlForm actionUrl=springUrl(submitProjectUrl)>
      <@fdsAction.button buttonText="Submit" buttonValue="submit" />
      <@fdsAction.link linkText="Back to task list" linkClass="govuk-link govuk-link--button" linkUrl=springUrl(taskListUrl)/>
    </@fdsForm.htmlForm>
  </#if>
</@defaultPageWithSidebar.defaultPageWithSidebar>

<#macro _invalidProjectInset>
  <@fdsInsetText.insetText insetTextClass="govuk-inset-text--red">
    <p>You cannot submit your project until all sections shown on the task list are completed</p>
    <@fdsAction.link linkText="Back to task list" linkUrl=springUrl(taskListUrl)/>
  </@fdsInsetText.insetText>
</#macro>
