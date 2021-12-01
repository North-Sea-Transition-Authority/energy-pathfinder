<#include '../../layout.ftl'>

<#macro _projectSubmissionDetails
  submissionDate
  submittedByUserName
  submittedByUserEmailAddress
  submissionDatePrompt="Submission date"
  submittedByUserNamePrompt="Submitted by"
  submittedByUserEmailAddressPrompt="Submitter email"
>
  <@fdsDataItems.dataItem>
    <@fdsDataItems.dataValues key=submissionDatePrompt value=submissionDate />
    <@fdsDataItems.dataValues key=submittedByUserNamePrompt value=submittedByUserName />
    <@_submitterEmailAddressDataItem
      submitterEmailAddressPrompt=submittedByUserEmailAddressPrompt
      submitterEmailAddressValue=submittedByUserEmailAddress
    />
  </@fdsDataItems.dataItem>
</#macro>

<#macro _submitterEmailAddressDataItem submitterEmailAddressPrompt submitterEmailAddressValue>
  <#assign emailAddress>
    <@fdsAction.link linkText=submitterEmailAddressValue linkUrl="mailto:${submitterEmailAddressValue}"/>
  </#assign>
  <@fdsDataItems.dataValues key=submitterEmailAddressPrompt value=emailAddress />
</#macro>