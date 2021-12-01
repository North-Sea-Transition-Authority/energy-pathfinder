<#include '../../layout.ftl'>
<#import '_upcomingTenderSummary.ftl' as upcomingTenderSummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if upcomingTenderDiffModel?has_content>
    <#list upcomingTenderDiffModel as upcomingTenderDiff>
      <@upcomingTenderSummary.upcomingTenderDiffSummary
        diffModel=upcomingTenderDiff.upcomingTenderDiff
        files=upcomingTenderDiff.upcomingTenderFiles
        showHeader=true
        showActions=false
        headingSize="h3"
        headingClass="govuk-heading-m"
      />
    </#list>
  <#else>
    <@emptySectionSummaryInset.emptySectionSummaryInset
      itemText="upcoming tenders"
      projectTypeDisplayName=projectTypeDisplayNameLowercase
    />
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>