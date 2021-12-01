<#include '../../layout.ftl'>
<#import '_projectSubmissionDetails.ftl' as projectSubmissionDetails>

<@fdsDataItems.dataItem>
  <@fdsDataItems.dataValues key="Field stage" value=projectManagementDetailView.fieldStage!"" />
  <#if !projectManagementDetailView.isEnergyTransitionProject>
    <@fdsDataItems.dataValues key="Field" value=projectManagementDetailView.field!"" />
    <@fdsDataItems.dataValues key="Status" value=projectManagementDetailView.status />
  <#else>
    <@fdsDataItems.dataValues key="Status" value=projectManagementDetailView.status />
    <@fdsDataItems.dataValues key="" value="" />
  </#if>
</@fdsDataItems.dataItem>
<@projectSubmissionDetails._projectSubmissionDetails
  submissionDate=projectManagementDetailView.submissionDate
  submittedByUserName=projectManagementDetailView.submittedByUser
  submittedByUserEmailAddress=projectManagementDetailView.submittedByUserEmail
/>