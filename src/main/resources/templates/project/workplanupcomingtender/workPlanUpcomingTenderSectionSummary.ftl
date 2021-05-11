<#include '../../layout.ftl'>
<#import '_workPlanUpcomingTenderSummary.ftl' as upcomingTenderSummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if upcomingTendersDiffModel?has_content>
    <#list upcomingTendersDiffModel as upcomingTenderDiff>
      <@upcomingTenderSummary.upcomingTenderDiffSummary
        upcomingTenderDiff=upcomingTenderDiff
        showHeader=true
        showActions=false
        headingSize="h3"
        headingClass="govuk-heading-m"
      />
    </#list>
  <#else>
    <@emptySectionSummaryInset.emptySectionSummaryInset itemText="upcoming tenders"/>
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>