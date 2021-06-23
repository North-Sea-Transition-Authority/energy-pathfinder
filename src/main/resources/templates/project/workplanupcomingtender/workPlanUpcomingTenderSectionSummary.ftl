<#include '../../layout.ftl'>
<#import '_workPlanUpcomingTenderSummary.ftl' as upcomingTenderSummary>

<#-- @ftlvariable name="sectionId" type="String" -->
<#-- @ftlvariable name="sectionTitle" type="String" -->
<#-- @ftlvariable name="upcomingTendersDiffModel" type="java.util.List<java.util.Map<String, Object>>" -->
<#-- @ftlvariable name="workPlanTenderSetupDiffModel" type="java.util.Map<String, Object>" -->

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
    <@fdsCheckAnswers.checkAnswersWrapper summaryListId="${sectionId}-setup-answers">
      <@fdsCheckAnswers.checkAnswers>
        <@checkAnswers.checkAnswersStandardOrDiffRow
          prompt="Upcoming tenders to add"
          fieldValue=workPlanTenderSetupDiffModel.ForwardWorkPlanTenderSetupView_hasTendersToAdd
          isDiffedField=true
        />
      </@fdsCheckAnswers.checkAnswers>
    </@fdsCheckAnswers.checkAnswersWrapper>
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>