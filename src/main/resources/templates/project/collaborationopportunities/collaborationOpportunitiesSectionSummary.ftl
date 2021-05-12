<#include '../../layout.ftl'>
<#import '_collaborationOpportunitySummary.ftl' as collaborationOpportunitySummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if collaborationOpportunityDiffModel?has_content>
    <#list collaborationOpportunityDiffModel as collaborationOpportunityDiff>
      <@collaborationOpportunitySummary.collaborationOpportunityDiffSummary
        diffModel=collaborationOpportunityDiff.collaborationOpportunityDiff
        files=collaborationOpportunityDiff.collaborationOpportunityFiles
        showHeader=true
        showActions=false
        headingSize="h3"
        headingClass="govuk-heading-m"
      />
    </#list>
    <#else>
      <@emptySectionSummaryInset.emptySectionSummaryInset
        itemText="collaboration opportunities"
        projectTypeDisplayName=projectTypeDisplayNameLowercase
      />
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
