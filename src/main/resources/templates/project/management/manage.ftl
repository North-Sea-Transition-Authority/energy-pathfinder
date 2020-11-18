<#include '../../layout.ftl'>
<#import '../summary/projectSummary.ftl' as projectSummary/>

<@defaultPage htmlTitle="Manage project" fullWidthColumn=true twoThirdsColumn=false>
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
