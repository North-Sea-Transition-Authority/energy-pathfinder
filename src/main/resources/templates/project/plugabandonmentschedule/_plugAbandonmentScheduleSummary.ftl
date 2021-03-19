<#include '../../layout.ftl'>

<#assign idPrefix = "plug-abandonment-schedule" />
<#assign headingPrefix = "Well decommissioning schedule" />
<#assign defaultHeadingSize = "h2" />
<#assign defaultHeadingClass = "govuk-heading-l" />

<#macro plugAbandonmentScheduleSummary
  plugAbandonmentScheduleView
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=plugAbandonmentScheduleView.displayOrder
    isValid=plugAbandonmentScheduleView.valid!""
    summaryLinkList=plugAbandonmentScheduleView.summaryLinks
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_plugAbandonmentScheduleSummaryFields
      useDiffedField=false
      earliestStartYear=plugAbandonmentScheduleView.earliestStartYear
      latestCompletionYear=plugAbandonmentScheduleView.latestCompletionYear
      wells=plugAbandonmentScheduleView.wells
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro plugAbandonmentScheduleDiffSummary
  plugAbandonmentScheduleDiff
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=plugAbandonmentScheduleDiff.PlugAbandonmentScheduleView_displayOrder.currentValue
    isValid=true
    summaryLinkList=[]
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_plugAbandonmentScheduleSummaryFields
      useDiffedField=true
      earliestStartYear=plugAbandonmentScheduleDiff.PlugAbandonmentScheduleView_earliestStartYear
      latestCompletionYear=plugAbandonmentScheduleDiff.PlugAbandonmentScheduleView_latestCompletionYear
      wells=plugAbandonmentScheduleDiff.PlugAbandonmentScheduleView_wells
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro _plugAbandonmentScheduleSummaryFields
  useDiffedField
  earliestStartYear=""
  latestCompletionYear=""
  wells=""
>
  <@decomissioningPeriodCheckAnswers.decomissioningPeriodCheckAnswers
    useDiffedField=useDiffedField
    prompt="Plug abandonment period"
    earliestYear=earliestStartYear
    latestYear=latestCompletionYear
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
