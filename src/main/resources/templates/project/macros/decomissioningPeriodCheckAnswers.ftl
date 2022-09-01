<#include '../../layout.ftl'>

<#macro decomissioningPeriodCheckAnswers useDiffedField prompt earliestYear latestYear>
  <@minMaxCheckAnswers.minMaxCheckAnswerRow
    useDiffedField=useDiffedField
    prompt=prompt
    minValue=earliestYear
    maxValue=latestYear
  />
</#macro>
