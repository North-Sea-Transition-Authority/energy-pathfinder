<#include '../../layout.ftl'>

<#macro _projectSubmissionDetails
  submissionDate
  submittedByUserName
  submittedByUserEmailAddress
  submissionDatePrompt="Submission date"
  submittedByUserNamePrompt="Submitted by"
  submittedByUserEmailAddressPrompt="Submitter email"
>
  <#assign emailAddress>
    <@fdsAction.link linkText=submittedByUserEmailAddress linkUrl="mailto:${submittedByUserEmailAddress}"/>
  </#assign>
  <@fdsDataItems.dataItem>
    <@fdsDataItems.dataValues key=submissionDatePrompt value=submissionDate />
    <@fdsDataItems.dataValues key=submittedByUserNamePrompt value=submittedByUserName />
    <@fdsDataItems.dataValues key=submittedByUserEmailAddressPrompt value=emailAddress />
  </@fdsDataItems.dataItem>
</#macro>