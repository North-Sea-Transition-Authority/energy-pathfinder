<#include '../../layout.ftl'/>

<#macro checkAnswersRowNoActions prompt value>
  <@checkAnswersRowNoActionsWithNested prompt=prompt>
    <#if value?has_content>
      ${value}
    </#if>
  </@checkAnswersRowNoActionsWithNested>
</#macro>

<#macro checkAnswersRowNoActionsWithNested prompt>
  <#local nested><#nested/></#local>
  <@fdsCheckAnswers.checkAnswersRow keyText=prompt actionText="" actionUrl="" screenReaderActionText="">
    <#if nested?has_content>
      ${nested}
    </#if>
  </@fdsCheckAnswers.checkAnswersRow>
</#macro>

<#macro checkAnswersUploadedFileViewNoActions uploadedFileView>
  <@fdsCheckAnswers.checkAnswersRow keyText="Document" actionText="" actionUrl="" screenReaderActionText="">
    <#if uploadedFileView?has_content>
      <@fdsAction.link linkText=uploadedFileView.fileName linkUrl=springUrl(uploadedFileView.fileUrl) />
    </#if>
  </@fdsCheckAnswers.checkAnswersRow>
  <@fdsCheckAnswers.checkAnswersRow keyText="Document description" actionText="" actionUrl="" screenReaderActionText="">
    <#if uploadedFileView?has_content>
      ${uploadedFileView.fileDescription}
    </#if>
  </@fdsCheckAnswers.checkAnswersRow>
</#macro>