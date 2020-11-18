<#include '../../../layout.ftl'>

<@fdsDataItems.dataItem>
  <@fdsDataItems.dataValues key="Field stage" value=projectManagementDetailView.fieldStage />
  <@fdsDataItems.dataValues key="Field" value=projectManagementDetailView.field />
  <@fdsDataItems.dataValues key="Status" value=projectManagementDetailView.status />
</@fdsDataItems.dataItem>
<@fdsDataItems.dataItem>
  <@fdsDataItems.dataValues key="Submission date" value=projectManagementDetailView.submissionDate />
  <@fdsDataItems.dataValues key="Submitted by" value=projectManagementDetailView.submittedByUser />
  <@fdsDataItems.dataValues key="Submitter email" value=projectManagementDetailView.submittedByUserEmail />
</@fdsDataItems.dataItem>
