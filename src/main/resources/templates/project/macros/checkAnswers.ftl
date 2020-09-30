<#include '../../layout.ftl'/>

<#macro checkAnswersRowNoActions prompt value>
  <@fdsCheckAnswers.checkAnswersRow keyText=prompt actionText="" actionUrl="" screenReaderActionText="">
    <#if value?has_content>
      ${value}
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