<#include '../../layout.ftl'>
<#import '_upcomingTenderSummary.ftl' as upcomingTenderSummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if upcomingTenderViews?has_content>
    <#list upcomingTenderViews as upcomingTenderView>
      <@upcomingTenderSummary.upcomingTenderSummary
        view=upcomingTenderView
        showHeader=true
        showActions=false
        showTag=true
        headingSize="h3"
        headingClass="govuk-heading-m"
      />
    </#list>
  <#else>
    <@emptySectionSummaryInset.emptySectionSummaryInset itemText="upcoming tenders"/>
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
