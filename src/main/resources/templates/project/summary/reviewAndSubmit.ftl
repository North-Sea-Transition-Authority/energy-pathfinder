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
    <@differenceChanges.toggler/>
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