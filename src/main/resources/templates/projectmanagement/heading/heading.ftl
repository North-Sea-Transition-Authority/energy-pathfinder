<#include '../../layout.ftl'>

<@headingWithContent
  caption=""
  captionClass=""
  pageHeading=headingText
  pageHeadingClass="govuk-heading-xl"
>
  <#if captionText?has_content>
    <span class="govuk-caption-l">${captionText}</span>
  </#if>
</@headingWithContent>
