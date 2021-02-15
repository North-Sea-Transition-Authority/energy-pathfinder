<#include '../../layout.ftl'>

<#macro decomissioningPeriodCheckAnswers useDiffedField prompt earliestYear latestYear>
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
