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
      fpso=view.fpso
      infrastructureTypePrompt=view.infrastructureType
      infrastructureType=view.infrastructureType
      platformFpso=view.platformFpso
      fpsoType=view.fpsoType
      fpsoDimensions=view.fpsoDimensions
      topsideFpsoMass=view.topsideFpsoMass
      topsideRemovalEarliestYear=view.topsideRemovalEarliestYear
      topsideRemovalLatestYear=view.topsideRemovalLatestYear
      substructuresExpectedToBeRemoved=view.substructuresExpectedToBeRemoved?has_content?then(view.substructuresExpectedToBeRemoved?string("Yes", "No"), "")
      areSubstructuresExpectedToBeRemoved=view.substructuresExpectedToBeRemoved
      substructureRemovalPremise=view.substructureRemovalPremise
      substructureRemovalMass=view.substructureRemovalMass
      substructureRemovalEarliestYear=view.substructureRemovalEarliestYear
      substructureRemovalLatestYear=view.substructureRemovalLatestYear
      futurePlans=view.futurePlans
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro platformFpsoDiffSummary
  diffModel
  fpso
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
      fpso=fpso
      infrastructureTypePrompt=diffModel.PlatformFpsoView_infrastructureType.currentValue
      infrastructureType=diffModel.PlatformFpsoView_infrastructureType
      platformFpso=diffModel.PlatformFpsoView_platformFpso
      fpsoType=diffModel.PlatformFpsoView_fpsoType
      fpsoDimensions=diffModel.PlatformFpsoView_fpsoDimensions
      topsideFpsoMass=diffModel.PlatformFpsoView_topsideFpsoMass
      topsideRemovalEarliestYear=diffModel.PlatformFpsoView_topsideRemovalEarliestYear
      topsideRemovalLatestYear=diffModel.PlatformFpsoView_topsideRemovalLatestYear
      substructuresExpectedToBeRemoved=diffModel.PlatformFpsoView_substructuresExpectedToBeRemoved
      areSubstructuresExpectedToBeRemoved=areSubstructuresExpectedToBeRemoved
      substructureRemovalPremise=diffModel.PlatformFpsoView_substructureRemovalPremise
      substructureRemovalMass=diffModel.PlatformFpsoView_substructureRemovalMass
      substructureRemovalEarliestYear=diffModel.PlatformFpsoView_substructureRemovalEarliestYear
      substructureRemovalLatestYear=diffModel.PlatformFpsoView_substructureRemovalLatestYear
      futurePlans=diffModel.PlatformFpsoView_futurePlans
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro _platformFpsoSummaryFields
  useDiffedField
  fpso=false
  infrastructureTypePrompt=""
  infrastructureType=""
  platformFpso=""
  fpsoType=""
  fpsoDimensions=""
  topsideFpsoMass=""
  topsideRemovalEarliestYear=""
  topsideRemovalLatestYear=""
  substructuresExpectedToBeRemoved=""
  areSubstructuresExpectedToBeRemoved=false
  substructureRemovalPremise=""
  substructureRemovalMass=""
  substructureRemovalEarliestYear=""
  substructureRemovalLatestYear=""
  futurePlans=""
>
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Platform or FPSO"
    fieldValue=infrastructureType
    isDiffedField=useDiffedField
  />
  <#if infrastructureTypePrompt?has_content>
    <@checkAnswers.checkAnswersStandardNestedOrDiffRow
      prompt=infrastructureTypePrompt
      fieldValue=platformFpso
      isDiffedField=useDiffedField
    >
      <@stringWithTag.stringWithTag stringWithTag=platformFpso />
    </@checkAnswers.checkAnswersStandardNestedOrDiffRow>
  </#if>
  <#if fpso>
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
  </#if>
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Topside/FPSO removal mass"
    fieldValue=topsideFpsoMass
    isDiffedField=useDiffedField
  />
  <@decomissioningPeriodCheckAnswers.decomissioningPeriodCheckAnswers
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
    <@decomissioningPeriodCheckAnswers.decomissioningPeriodCheckAnswers
      useDiffedField=useDiffedField
      prompt="Substructure removal years"
      earliestYear=substructureRemovalEarliestYear
      latestYear=substructureRemovalLatestYear
    />
  </#if>
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Future plans"
    fieldValue=futurePlans
    isDiffedField=useDiffedField
  />
</#macro>
