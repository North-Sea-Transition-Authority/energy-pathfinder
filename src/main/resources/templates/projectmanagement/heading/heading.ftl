<#include '../../layout.ftl'>

<#-- @ftlvariable name="projectTitle" type="String" -->
<#-- @ftlvariable name="projectOperatorDisplayName" type="String" -->

<@headingWithContent
  caption=""
  captionClass=""
  pageHeading="Project: ${projectTitle}"
  pageHeadingClass="govuk-heading-xl"
>
  <span class="govuk-caption-l">${projectOperatorDisplayName}</span>
</@headingWithContent>
