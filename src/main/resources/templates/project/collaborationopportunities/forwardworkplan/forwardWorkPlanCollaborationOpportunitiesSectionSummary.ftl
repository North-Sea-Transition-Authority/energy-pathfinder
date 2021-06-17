<#include '../../../layout.ftl'>
<#import '_forwardWorkPlanCollaborationOpportunitySummary.ftl' as collaborationOpportunitySummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
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
</@sectionSummaryWrapper.sectionSummaryWrapper>
