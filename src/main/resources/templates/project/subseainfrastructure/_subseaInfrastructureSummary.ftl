<#include '../../layout.ftl'>

<#macro subseaInfrastructureSummary subseaInfrastructureView showHeader=true showActions=true headingSize="h2" headingClass="govuk-heading-l" showTag=false>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix="subsea-infrastructure"
    headingPrefix="Subsea infrastructure"
    summaryView=subseaInfrastructureView
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Host structure">
      <#if showTag>
        <@stringWithTag.stringWithTag stringWithTag=subseaInfrastructureView.structure />
      <#else>
        ${subseaInfrastructureView.structure.value!""}
      </#if>
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
    <@checkAnswers.checkAnswersRowNoActions prompt="Description" value=subseaInfrastructureView.description!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Structure status" value=subseaInfrastructureView.status!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Type of infrastructure" value=subseaInfrastructureView.infrastructureType!"" />
    <#if subseaInfrastructureView.concreteMattress>
      <@checkAnswers.checkAnswersRowNoActions
        prompt="Number of mattresses to decommission"
        value=subseaInfrastructureView.numberOfMattresses!""
      />
      <@checkAnswers.checkAnswersRowNoActions
        prompt="Total estimated mass"
        value=subseaInfrastructureView.totalEstimatedMattressMass!""
      />
    </#if>
    <#if subseaInfrastructureView.subseaStructure>
      <@checkAnswers.checkAnswersRowNoActions
        prompt="Total estimated mass"
        value=subseaInfrastructureView.totalEstimatedSubseaMass!""
      />
    </#if>
    <#if subseaInfrastructureView.otherInfrastructure>
      <@checkAnswers.checkAnswersRowNoActions
        prompt="Type of subsea structure being decommissioned"
        value=subseaInfrastructureView.otherInfrastructureType!""
      />
      <@checkAnswers.checkAnswersRowNoActions
        prompt="Total estimated mass"
        value=subseaInfrastructureView.totalEstimatedOtherMass!""
      />
    </#if>
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Decommissioning period">
      ${subseaInfrastructureView.earliestDecommissioningStartYear!""}
      <br/>
      ${subseaInfrastructureView.latestDecommissioningCompletionYear!""}
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>