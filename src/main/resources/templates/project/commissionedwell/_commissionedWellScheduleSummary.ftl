<#include '../../layout.ftl'>

<#assign idPrefix = "commissioned-well-schedule" />
<#assign headingPrefix = "Well commissioning schedule" />
<#assign defaultHeadingSize = "h2" />
<#assign defaultHeadingClass = "govuk-heading-l" />

<#macro commissionedWellScheduleSummary
  commissionedWellScheduleView
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=commissionedWellScheduleView.displayOrder
    isValid=commissionedWellScheduleView.valid!""
    summaryLinkList=commissionedWellScheduleView.summaryLinks
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_commissionedWellSummaryFields
      useDiffedField=false
      earliestStartYear=commissionedWellScheduleView.earliestStartYear
      latestCompletionYear=commissionedWellScheduleView.latestCompletionYear
      wells=commissionedWellScheduleView.wells
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro commissionedWellDiffSummary
  commissionedWellScheduleDiff
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=commissionedWellScheduleDiff.CommissionedWellScheduleView_displayOrder.currentValue
    isValid=true
    summaryLinkList=[]
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_commissionedWellSummaryFields
      useDiffedField=true
      earliestStartYear=commissionedWellScheduleDiff.CommissionedWellScheduleView_earliestStartYear
      latestCompletionYear=commissionedWellScheduleDiff.CommissionedWellScheduleView_latestCompletionYear
      wells=commissionedWellScheduleDiff.CommissionedWellScheduleView_wells
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro _commissionedWellSummaryFields
  useDiffedField
  earliestStartYear=""
  latestCompletionYear=""
  wells=[]
>
  <@minMaxCheckAnswers.minMaxCheckAnswerRow
    useDiffedField=useDiffedField
    prompt="Well commissioning period"
    minValue=earliestStartYear
    maxValue=latestCompletionYear
    />
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Wells">
      <#list wells as diffedWell>
        <div>
          <#if useDiffedField>
            <@differenceChanges.renderDifference
              diffedField=diffedWell
            />
          <#else>
            ${diffedWell}
          </#if>
        </div>
      </#list>
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
</#macro>
