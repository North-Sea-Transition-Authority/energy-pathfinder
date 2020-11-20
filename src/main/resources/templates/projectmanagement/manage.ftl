<#include '../layout.ftl'>
<#import '../project/summary/projectSummary.ftl' as projectSummary/>

<@defaultPage
  htmlTitle="Manage project"
  fullWidthColumn=true
  twoThirdsColumn=false
  backLink=true
  backLinkUrl=springUrl(backLinkUrl)
>
  <@headingWithContent
    caption=""
    captionClass=""
    pageHeading="Project: ${projectManagementView.title}"
    pageHeadingClass="govuk-heading-xl"
  >
    <span class="govuk-caption-l">${projectManagementView.operator}</span>
  </@headingWithContent>
  ${projectManagementView.sectionsHtml?no_esc}
</@defaultPage>
