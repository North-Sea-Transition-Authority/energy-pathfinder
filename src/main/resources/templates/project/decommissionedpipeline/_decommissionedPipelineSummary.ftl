<#include '../../layout.ftl'>

<#assign idPrefix = "pipeline" />
<#assign headingPrefix = "Pipeline" />
<#assign defaultHeadingSize = "h2" />
<#assign defaultHeadingClass = "govuk-heading-l" />

<#macro decommissionedPipelineSummary
  decommissionedPipelineView
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=decommissionedPipelineView.displayOrder
    isValid=decommissionedPipelineView.valid!""
    summaryLinkList=decommissionedPipelineView.summaryLinks
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_decommissionedPipelineSummaryFields
      useDiffedField=false
      pipeline=decommissionedPipelineView.pipeline
      status=decommissionedPipelineView.status
      decommissioningEarliestYear=decommissionedPipelineView.decommissioningEarliestYear
      decommissioningLatestYear=decommissionedPipelineView.decommissioningLatestYear
      removalPremise=decommissionedPipelineView.removalPremise
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro decommissionedPipelineDiffSummary
  diffModel
  decommissionedPipelineDiff
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=decommissionedPipelineDiff.DecommissionedPipelineView_displayOrder.currentValue
    isValid=true
    summaryLinkList=[]
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
    diffObject=diffModel
  >
    <@_decommissionedPipelineSummaryFields
      useDiffedField=true
      pipeline=decommissionedPipelineDiff.DecommissionedPipelineView_pipeline
      status=decommissionedPipelineDiff.DecommissionedPipelineView_status
      decommissioningEarliestYear=decommissionedPipelineDiff.DecommissionedPipelineView_decommissioningEarliestYear
      decommissioningLatestYear=decommissionedPipelineDiff.DecommissionedPipelineView_decommissioningLatestYear
      removalPremise=decommissionedPipelineDiff.DecommissionedPipelineView_removalPremise
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro _decommissionedPipelineSummaryFields
  useDiffedField
  pipeline=""
  status=""
  decommissioningEarliestYear=""
  decommissioningLatestYear=""
  removalPremise=""
>
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Pipeline"
    fieldValue=pipeline
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Pipeline status"
    fieldValue=status
    isDiffedField=useDiffedField
  />
  <@decomissioningPeriodCheckAnswers.decomissioningPeriodCheckAnswers
    useDiffedField=useDiffedField
    prompt="Expected decommissioning period"
    earliestYear=decommissioningEarliestYear
    latestYear=decommissioningLatestYear
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Decommissioning premise"
    fieldValue=removalPremise
    isDiffedField=useDiffedField
  />
</#macro>
