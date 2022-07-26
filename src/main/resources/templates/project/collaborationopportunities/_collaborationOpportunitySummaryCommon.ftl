<#include '../../layout.ftl'>

<#macro _collaborationOpportunitySummaryFields
  useDiffedField
  function=""
  descriptionOfWork=""
  urgentResponseNeeded=""
  contactName=""
  contactPhoneNumber=""
  contactJobTitle=""
  contactEmailAddress=""
  uploadedFileUrl=""
  uploadedFileName=""
  uploadedFileDescription=""
  addedByPortalOrganisationGroup=""
>
  <@checkAnswers.checkAnswersStandardNestedOrDiffRow
    prompt="Opportunity function"
    fieldValue=function
    isDiffedField=useDiffedField
  >
    <@stringWithTag.stringWithTag stringWithTag=function />
  </@checkAnswers.checkAnswersStandardNestedOrDiffRow>
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Description of work"
    fieldValue=descriptionOfWork
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Urgent response required"
    fieldValue=urgentResponseNeeded
    isDiffedField=useDiffedField
    />
    <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Name"
    fieldValue=contactName
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Phone number"
    fieldValue=contactPhoneNumber
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Job title"
    fieldValue=contactJobTitle
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersRowEmailOrDiff
    prompt="Email address"
    fieldValue=contactEmailAddress
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffUploadedFileViewRow
    fileUrlFieldValue=uploadedFileUrl
    fileNameFieldValue=uploadedFileName
    fileDescriptionFieldValue=uploadedFileDescription
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Added by"
    fieldValue=addedByPortalOrganisationGroup
    isDiffedField=useDiffedField
  />
</#macro>