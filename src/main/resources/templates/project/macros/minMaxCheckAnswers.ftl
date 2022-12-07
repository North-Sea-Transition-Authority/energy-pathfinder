<#include '../../layout.ftl'>

<#macro minMaxCheckAnswerRow useDiffedField prompt minValue maxValue>
  <@checkAnswers.checkAnswersRowNoActionsWithNested prompt=prompt>
    <#if useDiffedField>
      <@differenceChanges.renderDifference
        diffedField=minValue
      />
      <br/>
      <@differenceChanges.renderDifference
        diffedField=maxValue
      />
    <#else>
      <div>${minValue}</div>
      <div>${maxValue}</div>
    </#if>
  </@checkAnswers.checkAnswersRowNoActionsWithNested>
</#macro>
