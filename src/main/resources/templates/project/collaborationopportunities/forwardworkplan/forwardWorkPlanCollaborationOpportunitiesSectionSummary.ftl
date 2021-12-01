<#include '../../../layout.ftl'>
<#import '_forwardWorkPlanCollaborationOpportunitySummary.ftl' as collaborationOpportunitySummary>

<#-- @ftlvariable name="sectionId" type="String" -->
<#-- @ftlvariable name="sectionTitle" type="String" -->
<#-- @ftlvariable name="collaborationOpportunityDiffModel" type="java.util.List<java.util.Map<String, Object>>" -->
<#-- @ftlvariable name="workPlanCollaborationSetupDiffModel" type="java.util.Map<String, Object>" -->

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
    <@fdsCheckAnswers.checkAnswersWrapper summaryListId="${sectionId}-setup-answers">
      <@fdsCheckAnswers.checkAnswers>
        <@checkAnswers.checkAnswersStandardOrDiffRow
          prompt="Collaboration opportunities to add"
          fieldValue=workPlanCollaborationSetupDiffModel.ForwardWorkPlanCollaborationSetupView_hasCollaborationsToAdd
          isDiffedField=true
        />
      </@fdsCheckAnswers.checkAnswers>
    </@fdsCheckAnswers.checkAnswersWrapper>
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
