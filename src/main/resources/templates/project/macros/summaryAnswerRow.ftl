<#include '../../layout.ftl'/>

<#macro summaryAnswerRow prompt value>
  <@fdsCheckAnswers.checkAnswersRow keyText=prompt actionText="" actionUrl="" screenReaderActionText="">
    ${value}
  </@fdsCheckAnswers.checkAnswersRow>
</#macro>