<#include '../../layoutPane.ftl'>
<#import '../summary/projectSummary.ftl' as projectSummary/>

<#assign taskListTitle = "Back to task list" />
<#assign defaultPageHeading = "Overview" />

<@defaultPageWithSidebar.defaultPageWithSidebar
  pageHeading=""
  themeHeading=""
  sidebarSectionLinks=projectSummaryView.sidebarSectionLinks
  htmlTitle=defaultPageHeading
  isSidebarSticky=true
>
  <@defaultHeading
    caption=""
    captionClass=""
    pageHeading=defaultPageHeading
    pageHeadingClass="govuk-heading-xl"
  />
  <@projectSummary.summary projectSummaryView=projectSummaryView />
  <@fdsAction.link
    linkText=taskListTitle
    linkUrl=springUrl(taskListUrl)
    linkClass="govuk-link govuk-!-font-size-19"
  />
</@defaultPageWithSidebar.defaultPageWithSidebar>