<#include '../../layoutPane.ftl'>
<#import 'projectSummary.ftl' as projectSummary/>

<#if isUpdate>
  <#assign pageHeading="Review and submit project update" />
<#else>
  <#assign pageHeading="Review and submit project" />
</#if>

<@defaultPagePane htmlTitle=pageHeading phaseBanner=false>
  <@defaultPagePaneSubNav>
    <@fdsSubNavigation.subNavigation>
      <@fdsSubNavigation.subNavigationSection themeHeading="Check your answers for all sections on the project">
        <#list projectSummaryView.sidebarSectionLinks as sidebarLink>
          <@sideBarSectionLink.renderSidebarLink sidebarLink=sidebarLink/>
        </#list>
      </@fdsSubNavigation.subNavigationSection>
    </@fdsSubNavigation.subNavigation>
  </@defaultPagePaneSubNav>

  <@defaultPagePaneContent pageHeading=pageHeading>
    <#if !isProjectValid>
      <@invalidProjectInset />
    </#if>

    <@projectSummary.summary projectSummaryView=projectSummaryView />

    <#if !isProjectValid>
      <@invalidProjectInset />
    <#else>
      <@fdsForm.htmlForm actionUrl=springUrl(submitProjectUrl)>
        <@fdsAction.button buttonText="Submit" buttonValue="submit" />
        <@fdsAction.link linkText="Back to task list" linkClass="govuk-link govuk-link--button" linkUrl=springUrl(taskListUrl)/>
      </@fdsForm.htmlForm>
    </#if>
  </@defaultPagePaneContent>
</@defaultPagePane>


<#macro invalidProjectInset>
  <@fdsInsetText.insetText insetTextClass="govuk-inset-text--red">
    <p>You cannot submit your project until all sections shown on the task list are completed</p>
    <@fdsAction.link linkText="Back to task list" linkUrl=springUrl(taskListUrl)/>
  </@fdsInsetText.insetText>
</#macro>
