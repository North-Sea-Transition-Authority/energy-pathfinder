<#include '../../layoutPane.ftl'>
<#import 'projectSummary.ftl' as projectSummary/>

<#assign projectTypeDisplayNameLowercase = projectTypeDisplayNameLowercase />

<#assign defaultPageHeading = "Check your answers before submitting your ${projectTypeDisplayNameLowercase}" />

<#assign taskListTitle = "Back to task list" />

<#if isUpdate>
  <#assign pageHeading="${defaultPageHeading} update" />
<#else>
  <#assign pageHeading="${defaultPageHeading}" />
</#if>

<@defaultPageWithSidebar.defaultPageWithSidebar
  pageHeading=""
  themeHeading=""
  sidebarSectionLinks=projectSummaryView.sidebarSectionLinks
  htmlTitle=pageHeading
  isSidebarSticky=true
>

  <#if !isProjectValid>
    <@fdsError.singleErrorSummary errorMessage="You cannot submit your ${projectTypeDisplayNameLowercase}
     until all sections shown on the task list are completed"/>
  </#if>

  <@defaultHeading
    caption=""
    captionClass=""
    pageHeading=pageHeading
    pageHeadingClass="govuk-heading-xl"
    errorItems=errorItems
  />

  <#if isUpdate>
    <@_updateInfo />
  </#if>
  <@projectSummary.summary projectSummaryView=projectSummaryView />

  <#if isProjectValid>
    <@fdsForm.htmlForm actionUrl=springUrl(submitProjectUrl)>
      <@fdsAction.button buttonText="Submit" buttonValue="submit" />
      <@fdsAction.link linkText=taskListTitle linkClass="govuk-link govuk-link--button" linkUrl=springUrl(taskListUrl)/>
    </@fdsForm.htmlForm>
  <#else>
    <@fdsAction.link
      linkText=taskListTitle
      linkUrl=springUrl(taskListUrl)
      linkClass="govuk-link govuk-!-font-size-19"
    />
  </#if>

</@defaultPageWithSidebar.defaultPageWithSidebar>

<#macro _updateInfo>
  <#if updateRequestReason?has_content>
    <@fdsInsetText.insetText>
      <h2 class="govuk-heading-l">What was I asked to update?</h2>
      <@multiLineText.multiLineText blockClass="govuk-body">
        <div>${updateRequestReason}</div>
      </@multiLineText.multiLineText>
      <@differenceChanges.toggler analyticsEventCategory="SHOW_DIFFS_PROJECT" formGroupClass="govuk-!-margin-bottom-0"/>
    </@fdsInsetText.insetText>
  <#else>
    <@differenceChanges.toggler analyticsEventCategory="SHOW_DIFFS_PROJECT"/>
  </#if>
</#macro>