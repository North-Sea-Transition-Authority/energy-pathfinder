<#include '../../layout.ftl'/>

<#macro checkAnswersRowNoActions prompt value multiLineTextBlockClass="govuk-body">
  <@checkAnswersRowNoActionsWithNested prompt=prompt>
    <#if value?has_content>
      <@multiLineText.multiLineText blockClass=multiLineTextBlockClass>${value}</@multiLineText.multiLineText>
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

<#macro checkAnswersUploadedFileViewNoActions fileUrlFieldValue fileNameFieldValue fileDescriptionFieldValue>
  <@fdsCheckAnswers.checkAnswersRowNoAction keyText="Document">
    <#if fileUrlFieldValue?has_content && fileNameFieldValue?has_content>
      <@fdsAction.link linkText=fileNameFieldValue linkUrl=springUrl(fileUrlFieldValue) />
    </#if>
  </@fdsCheckAnswers.checkAnswersRowNoAction>
  <@fdsCheckAnswers.checkAnswersRowNoAction keyText="Document description">
    <#if fileDescriptionFieldValue?has_content>
      ${fileDescriptionFieldValue}
    </#if>
  </@fdsCheckAnswers.checkAnswersRowNoAction>
</#macro>

<#macro diffedCheckAnswersUploadedFileViewNoAction fileUrlFieldValue fileNameFieldValue fileDescriptionFieldValue>
  <@fdsCheckAnswers.checkAnswersRowNoAction keyText="Document">
    <#if fileUrlFieldValue?has_content && fileNameFieldValue?has_content>
      <@differenceChanges.renderDifferenceLink
        differenceType=fileUrlFieldValue.differenceType
        diffedLinkText=fileNameFieldValue
        diffedLinkUrl=fileUrlFieldValue
      />
    </#if>
  </@fdsCheckAnswers.checkAnswersRowNoAction>
  <@diffedCheckAnswersRowNoActions prompt="Document description" diffedField=fileDescriptionFieldValue />
</#macro>

<#macro checkAnswersStandardOrDiffUploadedFileViewRow
  fileUrlFieldValue
  fileNameFieldValue
  fileDescriptionFieldValue
  isDiffedField
>
  <#if isDiffedField>
    <@diffedCheckAnswersUploadedFileViewNoAction
      fileUrlFieldValue=fileUrlFieldValue
      fileNameFieldValue=fileNameFieldValue
      fileDescriptionFieldValue=fileDescriptionFieldValue
    />
  <#else>
    <@checkAnswersUploadedFileViewNoActions
      fileUrlFieldValue=fileUrlFieldValue
      fileNameFieldValue=fileNameFieldValue
      fileDescriptionFieldValue=fileDescriptionFieldValue
    />
  </#if>

</#macro>

<#macro diffedCheckAnswersRowNoActions prompt diffedField multiLineTextBlockClass="govuk-body">
  <@checkAnswersRowNoActionsWithNested prompt=prompt>
    <#if diffedField?has_content>
      <@differenceChanges.renderDifference
        diffedField=diffedField
        multiLineTextBlockClass=multiLineTextBlockClass
      />
    </#if>
  </@checkAnswersRowNoActionsWithNested>
</#macro>

<#macro checkAnswersStandardOrDiffRow prompt fieldValue isDiffedField multiLineTextBlockClass="govuk-body">
  <#if isDiffedField>
    <@diffedCheckAnswersRowNoActions
      prompt=prompt
      diffedField=fieldValue
      multiLineTextBlockClass=multiLineTextBlockClass
    />
  <#else>
    <@checkAnswersRowNoActions prompt=prompt value=fieldValue />
  </#if>
</#macro>

<#macro checkAnswersStandardNestedOrDiffRow prompt fieldValue isDiffedField multiLineTextBlockClass="govuk-body">
  <#if isDiffedField>
    <@diffedCheckAnswersRowNoActions
      prompt=prompt
      diffedField=fieldValue
      multiLineTextBlockClass=multiLineTextBlockClass
    />
  <#else>
    <@checkAnswersRowNoActionsWithNested prompt=prompt>
      <#nested/>
    </@checkAnswersRowNoActionsWithNested>
  </#if>
</#macro>