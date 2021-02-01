<#include '../../layout.ftl'/>

<#assign idPrefix = "platform-fpso" />
<#assign headingPrefix = "Platform or FPSO" />
<#assign defaultPlatformFpsoName = "Platform or FPSO" />
<#assign defaultHeadingSize = "h2" />
<#assign defaultHeadingClass = "govuk-heading-l" />

<#macro platformFpsoSummary
  view
  platformFpsoName=defaultPlatformFpsoName
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=view.displayOrder
    isValid=view.valid!""
    summaryLinkList=view.summaryLinks
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_platformFpsoSummaryFields
      useDiffedField=false
      platformFpso=view.platformFpso
      topsideFpsoMass=view.topsideFpsoMass
      topsideRemovalEarliestYear=view.topsideRemovalEarliestYear
      topsideRemovalLatestYear=view.topsideRemovalLatestYear
      substructuresExpectedToBeRemoved=view.substructuresExpectedToBeRemoved?has_content?then(view.substructuresExpectedToBeRemoved?string("Yes", "No"), "")
      areSubstructuresExpectedToBeRemoved=view.substructuresExpectedToBeRemoved
      substructureRemovalPremise=view.substructureRemovalPremise
      substructureRemovalMass=view.substructureRemovalMass
      substructureRemovalEarliestYear=view.substructureRemovalEarliestYear
      substructureRemovalLatestYear=view.substructureRemovalLatestYear
      fpsoType=view.fpsoType
      fpsoDimensions=view.fpsoDimensions
      futurePlans=view.futurePlans
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro platformFpsoDiffSummary
  diffModel
  areSubstructuresExpectedToBeRemoved
  platformFpsoName=defaultPlatformFpsoName
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=diffModel.PlatformFpsoView_displayOrder.currentValue
    isValid=true
    summaryLinkList=[]
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_platformFpsoSummaryFields
      useDiffedField=true
      platformFpso=diffModel.PlatformFpsoView_platformFpso
      topsideFpsoMass=diffModel.PlatformFpsoView_topsideFpsoMass
      topsideRemovalEarliestYear=diffModel.PlatformFpsoView_topsideRemovalEarliestYear
      topsideRemovalLatestYear=diffModel.PlatformFpsoView_topsideRemovalLatestYear
      substructuresExpectedToBeRemoved=diffModel.PlatformFpsoView_substructuresExpectedToBeRemoved
      areSubstructuresExpectedToBeRemoved=areSubstructuresExpectedToBeRemoved
      substructureRemovalPremise=diffModel.PlatformFpsoView_substructureRemovalPremise
      substructureRemovalMass=diffModel.PlatformFpsoView_substructureRemovalMass
      substructureRemovalEarliestYear=diffModel.PlatformFpsoView_substructureRemovalEarliestYear
      substructureRemovalLatestYear=diffModel.PlatformFpsoView_substructureRemovalLatestYear
      fpsoType=diffModel.PlatformFpsoView_fpsoType
      fpsoDimensions=diffModel.PlatformFpsoView_fpsoDimensions
      futurePlans=diffModel.PlatformFpsoView_futurePlans
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro _platformFpsoSummaryFields
  useDiffedField
  platformFpso=""
  topsideFpsoMass=""
  topsideRemovalEarliestYear=""
  topsideRemovalLatestYear=""
  substructuresExpectedToBeRemoved=""
  areSubstructuresExpectedToBeRemoved=false
  substructureRemovalPremise=""
  substructureRemovalMass=""
  substructureRemovalEarliestYear=""
  substructureRemovalLatestYear=""
  fpsoType=""
  fpsoDimensions=""
  futurePlans=""
>
  <@checkAnswers.checkAnswersStandardNestedOrDiffRow
    prompt="Platform or FPSO"
    fieldValue=platformFpso
    isDiffedField=useDiffedField
  >
    <@stringWithTag.stringWithTag stringWithTag=platformFpso />
  </@checkAnswers.checkAnswersStandardNestedOrDiffRow>
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Topside/FPSO removal mass"
    fieldValue=topsideFpsoMass
    isDiffedField=useDiffedField
  />
  <@_decomissioningPeriodCheckAnswers
    useDiffedField=useDiffedField
    prompt="Expected topside removal period"
    earliestYear=topsideRemovalEarliestYear
    latestYear=topsideRemovalLatestYear
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Substructures expected to be removed"
    fieldValue=substructuresExpectedToBeRemoved
    isDiffedField=useDiffedField
  />
  <#if areSubstructuresExpectedToBeRemoved>
    <@checkAnswers.checkAnswersStandardOrDiffRow
      prompt="Substructure removal premise"
      fieldValue=substructureRemovalPremise
      isDiffedField=useDiffedField
    />
    <@checkAnswers.checkAnswersStandardOrDiffRow
      prompt="Substructure removal mass"
      fieldValue=substructureRemovalMass
      isDiffedField=useDiffedField
    />
    <@_decomissioningPeriodCheckAnswers
      useDiffedField=useDiffedField
      prompt="Substructure removal years"
      earliestYear=substructureRemovalEarliestYear
      latestYear=substructureRemovalLatestYear
    />
  </#if>
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="FPSO type"
    fieldValue=fpsoType
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="FPSO dimensions"
    fieldValue=fpsoDimensions
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Future plans"
    fieldValue=futurePlans
    isDiffedField=useDiffedField
  />
</#macro>

<#macro _decomissioningPeriodCheckAnswers useDiffedField prompt earliestYear latestYear>
  <@checkAnswers.checkAnswersRowNoActionsWithNested prompt=prompt>
    <#if useDiffedField>
      <@differenceChanges.renderDifference
        diffedField=earliestYear
      />
      <br/>
      <@differenceChanges.renderDifference
        diffedField=latestYear
      />
    <#else>
      <div>${earliestYear}</div>
      <div>${latestYear}</div>
    </#if>
  </@checkAnswers.checkAnswersRowNoActionsWithNested>
</#macro>
