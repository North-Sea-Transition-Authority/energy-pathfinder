<#macro sectionSummaryWrapper sectionId sectionTitle headingClass="">
  <h2 class="govuk-heading-l summary-list__heading ${headingClass}" id=${sectionId} >${sectionTitle}</h2>
  <#nested>
</#macro>

<#macro sectionSummaryListWrapper sectionId sectionTitle>
  <@sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle headingClass="govuk-!-margin-bottom-6">
    <#nested>
  </@sectionSummaryWrapper>
</#macro>
