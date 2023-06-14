<#include '../../layout.ftl'>

<#assign idPrefix = "integrated-rig" />
<#assign headingPrefix = "Integrated rig" />
<#assign defaultHeadingSize = "h2" />
<#assign defaultHeadingClass = "govuk-heading-l" />

<#macro integratedRigSummary
  integratedRigView
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=integratedRigView.displayOrder
    isValid=integratedRigView.valid!""
    summaryLinkList=integratedRigView.summaryLinks
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_integratedRigSummaryFields
      useDiffedField=false
      structure=integratedRigView.structure
      name=integratedRigView.name
      status=integratedRigView.status
      intentionToReactivate=integratedRigView.intentionToReactivate
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro integratedRigDiffSummary
  integratedRigDiff
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=integratedRigDiff.IntegratedRigView_displayOrder.currentValue
    isValid=true
    summaryLinkList=[]
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
    diffObject=integratedRigDiff
  >
    <@_integratedRigSummaryFields
      useDiffedField=true
      structure=integratedRigDiff.IntegratedRigView_structure
      name=integratedRigDiff.IntegratedRigView_name
      status=integratedRigDiff.IntegratedRigView_status
      intentionToReactivate=integratedRigDiff.IntegratedRigView_intentionToReactivate
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro _integratedRigSummaryFields
  useDiffedField
  structure=""
  name=""
  status=""
  intentionToReactivate=""
>
  <@checkAnswers.checkAnswersStandardNestedOrDiffRow
    prompt="Structure"
    fieldValue=structure
    isDiffedField=useDiffedField
  >
    <@stringWithTag.stringWithTag stringWithTag=structure />
  </@checkAnswers.checkAnswersStandardNestedOrDiffRow>
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Name"
    fieldValue=name
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Integrated rig status"
    fieldValue=status
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Intention to reactivate"
    fieldValue=intentionToReactivate
    isDiffedField=useDiffedField
  />
</#macro>
