<#include '../../layout.ftl'>
<#import '_projectSubmissionDetails.ftl' as projectSubmissionDetails>

<@projectSubmissionDetails._projectSubmissionDetails
  submissionDate=projectManagementDetailView.submissionDate
  submittedByUserName=projectManagementDetailView.submittedByUser
  submittedByUserEmailAddress=projectManagementDetailView.submittedByUserEmail
/>
<@fdsDataItems.dataItem>
  <@fdsDataItems.dataValues key="Status" value=projectManagementDetailView.status />
</@fdsDataItems.dataItem>