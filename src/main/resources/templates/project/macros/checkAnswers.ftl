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
  <@fdsCheckAnswers.checkAnswersRowNoAction keyText=prompt>
    <#if nested?has_content>
      ${nested}
    </#if>
  </@fdsCheckAnswers.checkAnswersRowNoAction>
</#macro>

<#macro checkAnswersUploadedFileViewNoActions uploadedFileView>
  <@fdsCheckAnswers.checkAnswersRowNoAction keyText="Document">
    <#if uploadedFileView?has_content>
      <@fdsAction.link linkText=uploadedFileView.fileName linkUrl=springUrl(uploadedFileView.fileUrl) />
    </#if>
  </@fdsCheckAnswers.checkAnswersRowNoAction>
  <@fdsCheckAnswers.checkAnswersRowNoAction keyText="Document description">
    <#if uploadedFileView?has_content>
      ${uploadedFileView.fileDescription}
    </#if>
  </@fdsCheckAnswers.checkAnswersRowNoAction>
</#macro>