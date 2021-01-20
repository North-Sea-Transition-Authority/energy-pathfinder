<#include '../../layout.ftl'/>

<#macro platformFpsoSummary view platformFpsoName="Platform or FPSO" showHeader=false showActions=false headingSize="h2" headingClass="govuk-heading-l" showTag=false>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix="platform-fpso"
    headingPrefix="Platform or FPSO"
    displayOrder=view.displayOrder
    isValid=view.valid!""
    summaryLinkList=view.summaryLinks
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Platform or FPSO">
      <#if showTag>
        <@stringWithTag.stringWithTag stringWithTag=view.platformFpso />
      <#else>
        ${view.platformFpso.value!""}
      </#if>
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
    <@checkAnswers.checkAnswersRowNoActions prompt="Topside/FPSO removal mass" value=view.topsideFpsoMass!"" />
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Expected topside removal period">
      ${view.topsideRemovalEarliestYear!""}
      <br/>
        ${view.topsideRemovalLatestYear!""}
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
    <@checkAnswers.checkAnswersRowNoActions
      prompt="Substructures expected to be removed"
      value=view.substructuresExpectedToBeRemoved?has_content?then(view.substructuresExpectedToBeRemoved?string("Yes", "No"), "")
    />
    <#if view.substructuresExpectedToBeRemoved?has_content && view.substructuresExpectedToBeRemoved>
      <@checkAnswers.checkAnswersRowNoActions prompt="Substructure removal premise" value=view.substructureRemovalPremise!"" />
      <@checkAnswers.checkAnswersRowNoActions prompt="Substructure removal mass" value=view.substructureRemovalMass!"" />
      <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Substructure removal years">
        ${view.substructureRemovalEarliestYear!""}
        <br/>
        ${view.substructureRemovalLatestYear!""}
      </@checkAnswers.checkAnswersRowNoActionsWithNested>
    </#if>
    <@checkAnswers.checkAnswersRowNoActions prompt="FPSO type" value=view.fpsoType!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="FPSO dimensions" value=view.fpsoDimensions!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Future plans" value=view.futurePlans!"" />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>