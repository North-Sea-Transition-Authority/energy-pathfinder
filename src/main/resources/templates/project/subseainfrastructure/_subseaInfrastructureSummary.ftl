<#include '../../layout.ftl'>

<#assign idPrefix = "subsea-infrastructure" />
<#assign headingPrefix = "Subsea infrastructure" />
<#assign defaultHeadingSize = "h2" />
<#assign defaultHeadingClass = "govuk-heading-l" />

<#macro subseaInfrastructureSummary
  subseaInfrastructureView
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=subseaInfrastructureView.displayOrder
    isValid=subseaInfrastructureView.valid!""
    summaryLinkList=subseaInfrastructureView.summaryLinks
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_subseaInfrastructureSummaryFields
      useDiffedField=false
      structure=subseaInfrastructureView.structure
      description=subseaInfrastructureView.description
      status=subseaInfrastructureView.status
      infrastructureType=subseaInfrastructureView.infrastructureType
      concreteMattress=subseaInfrastructureView.concreteMattress
      numberOfMattresses=subseaInfrastructureView.numberOfMattresses
      totalEstimatedMattressMass=subseaInfrastructureView.totalEstimatedMattressMass
      subseaStructure=subseaInfrastructureView.subseaStructure
      totalEstimatedSubseaMass=subseaInfrastructureView.totalEstimatedSubseaMass
      otherInfrastructure=subseaInfrastructureView.otherInfrastructure
      otherInfrastructureType=subseaInfrastructureView.otherInfrastructureType
      totalEstimatedOtherMass=subseaInfrastructureView.totalEstimatedOtherMass
      earliestDecommissioningStartYear=subseaInfrastructureView.earliestDecommissioningStartYear
      latestDecommissioningCompletionYear=subseaInfrastructureView.latestDecommissioningCompletionYear
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro subseaInfrastructureDiffSummary
  diffModel
  concreteMattress
  subseaStructure
  otherInfrastructure
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=diffModel.SubseaInfrastructureView_displayOrder.currentValue
    isValid=true
    summaryLinkList=[]
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_subseaInfrastructureSummaryFields
      useDiffedField=true
      structure=diffModel.SubseaInfrastructureView_structure
      description=diffModel.SubseaInfrastructureView_description
      status=diffModel.SubseaInfrastructureView_status
      infrastructureType=diffModel.SubseaInfrastructureView_infrastructureType
      concreteMattress=concreteMattress
      numberOfMattresses=diffModel.SubseaInfrastructureView_numberOfMattresses
      totalEstimatedMattressMass=diffModel.SubseaInfrastructureView_totalEstimatedMattressMass
      subseaStructure=subseaStructure
      totalEstimatedSubseaMass=diffModel.SubseaInfrastructureView_totalEstimatedSubseaMass
      otherInfrastructure=otherInfrastructure
      otherInfrastructureType=diffModel.SubseaInfrastructureView_otherInfrastructureType
      totalEstimatedOtherMass=diffModel.SubseaInfrastructureView_totalEstimatedOtherMass
      earliestDecommissioningStartYear=diffModel.SubseaInfrastructureView_earliestDecommissioningStartYear
      latestDecommissioningCompletionYear=diffModel.SubseaInfrastructureView_latestDecommissioningCompletionYear
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro _subseaInfrastructureSummaryFields
  useDiffedField
  structure=""
  description=""
  status=""
  infrastructureType=""
  concreteMattress=false
  numberOfMattresses=""
  totalEstimatedMattressMass=""
  subseaStructure=false
  totalEstimatedSubseaMass=""
  otherInfrastructure=false
  otherInfrastructureType=""
  totalEstimatedOtherMass=""
  earliestDecommissioningStartYear=""
  latestDecommissioningCompletionYear=""
>
  <@checkAnswers.checkAnswersStandardNestedOrDiffRow
    prompt="Surface infrastructure"
    fieldValue=structure
    isDiffedField=useDiffedField
  >
    <@stringWithTag.stringWithTag stringWithTag=structure />
  </@checkAnswers.checkAnswersStandardNestedOrDiffRow>
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Description"
    fieldValue=description
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Structure status"
    fieldValue=status
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Type of infrastructure"
    fieldValue=infrastructureType
    isDiffedField=useDiffedField
  />
  <#if concreteMattress>
    <@checkAnswers.checkAnswersStandardOrDiffRow
      prompt="Number of mattresses to decommission"
      fieldValue=numberOfMattresses
      isDiffedField=useDiffedField
    />
    <@checkAnswers.checkAnswersStandardOrDiffRow
      prompt="Total estimated mass"
      fieldValue=totalEstimatedMattressMass
      isDiffedField=useDiffedField
    />
  </#if>
  <#if subseaStructure>
    <@checkAnswers.checkAnswersStandardOrDiffRow
      prompt="Total estimated mass"
      fieldValue=totalEstimatedSubseaMass
      isDiffedField=useDiffedField
    />
  </#if>
  <#if otherInfrastructure>
    <@checkAnswers.checkAnswersStandardOrDiffRow
      prompt="Type of subsea structure being decommissioned"
      fieldValue=otherInfrastructureType
      isDiffedField=useDiffedField
    />
    <@checkAnswers.checkAnswersStandardOrDiffRow
      prompt="Total estimated mass"
      fieldValue=totalEstimatedOtherMass
      isDiffedField=useDiffedField
    />
  </#if>
  <@decomissioningPeriodCheckAnswers.decomissioningPeriodCheckAnswers
    useDiffedField=useDiffedField
    prompt="Expected decommissioning period"
    earliestYear=earliestDecommissioningStartYear
    latestYear=latestDecommissioningCompletionYear
  />
</#macro>