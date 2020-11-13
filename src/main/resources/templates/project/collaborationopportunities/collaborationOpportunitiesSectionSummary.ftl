<#include '../../layout.ftl'>
<#import '_collaborationOpportunitySummary.ftl' as collaborationOpportunitySummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if collaborationOpportunityViews?has_content>
    <#list collaborationOpportunityViews as collaborationOpportunityView>
      <@collaborationOpportunitySummary.collaborationOpportunitySummary
        view=collaborationOpportunityView
        showHeader=true
        showActions=false
        showTag=true
        headingSize="h3"
        headingClass="govuk-heading-m"
      />
    </#list>
    <#else>
      <@emptySectionSummaryInset.emptySectionSummaryInset itemText="collaboration opportunities"/>
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
