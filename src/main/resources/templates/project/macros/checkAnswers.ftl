<#include '../../layout.ftl'/>

<#macro checkAnswersRowNoActions prompt value>
  <@fdsCheckAnswers.checkAnswersRow keyText=prompt actionText="" actionUrl="" screenReaderActionText="">
    <#if value?has_content>
      ${value}
    </#if>
  </@fdsCheckAnswers.checkAnswersRow>
</#macro>