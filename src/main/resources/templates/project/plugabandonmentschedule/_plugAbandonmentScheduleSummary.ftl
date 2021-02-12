<#include '../../layout.ftl'>

<#assign idPrefix = "plug-abandonment-schedule" />
<#assign headingPrefix = "Plug abandonment schedule" />
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

<#macro _plugAbandonmentScheduleSummaryFields
  useDiffedField
  earliestStartYear=""
  latestCompletionYear=""
  wells=""
>
  <@_decomissioningPeriodCheckAnswers
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
