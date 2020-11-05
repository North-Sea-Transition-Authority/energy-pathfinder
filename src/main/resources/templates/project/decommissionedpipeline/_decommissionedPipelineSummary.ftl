<#include '../../layout.ftl'>

<#macro decommissionedPipelineSummary decommissionedPipelineView showHeader=false showActions=false headingSize="h2" headingClass="govuk-heading-l">
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix="pipeline"
    headingPrefix="Pipeline"
    summaryView=decommissionedPipelineView
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@checkAnswers.checkAnswersRowNoActions prompt="Pipeline" value=decommissionedPipelineView.pipeline!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Material type" value=decommissionedPipelineView.materialType!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Pipeline status" value=decommissionedPipelineView.status!"" />
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Decommissioning period">
      ${decommissionedPipelineView.decommissioningEarliestYear!""}
      <br/>
      ${decommissionedPipelineView.decommissioningLatestYear!""}
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
    <@checkAnswers.checkAnswersRowNoActions prompt="Removal premise" value=decommissionedPipelineView.removalPremise!"" />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>
