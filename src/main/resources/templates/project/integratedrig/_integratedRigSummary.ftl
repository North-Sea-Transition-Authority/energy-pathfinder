<#include '../../layout.ftl'>

<#macro integratedRigSummary
  integratedRigView
  showHeader=false
  showActions=false
  showTag=false
  headingSize="h2"
  headingClass="govuk-heading-l"
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix="integrated-rig"
    headingPrefix="Integrated rig"
    summaryView=integratedRigView
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Structure">
      <#if showTag>
        <@stringWithTag.stringWithTag stringWithTag=integratedRigView.structure />
      <#else>
        ${integratedRigView.structure.value!""}
      </#if>
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
    <@checkAnswers.checkAnswersRowNoActions prompt="Name" value=integratedRigView.name!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Integrated rig status" value=integratedRigView.status!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Intention to reactivate" value=integratedRigView.intentionToReactivate!"" />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>
