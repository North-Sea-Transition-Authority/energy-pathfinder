<#macro sectionSummaryWrapper sectionId sectionTitle headingClass="">
  <h2 class="govuk-heading-l ${headingClass}" id=${sectionId} >${sectionTitle}</h2>
  <#nested>
</#macro>
