<#include '../../../layout.ftl'>

<@fdsDataItems.dataItem>
  <@fdsDataItems.dataValues key="Field stage" value=projectManagementDetailsView.fieldStage />
  <@fdsDataItems.dataValues key="Field" value=projectManagementDetailsView.field />
  <@fdsDataItems.dataValues key="Current version" value=projectManagementDetailsView.version />
</@fdsDataItems.dataItem>
<@fdsDataItems.dataItem>
  <@fdsDataItems.dataValues key="Submission date" value=projectManagementDetailsView.submissionDate />
  <@fdsDataItems.dataValues key="Submitted by" value=projectManagementDetailsView.submittedByUser />
  <@fdsDataItems.dataValues key="Submitter email" value=projectManagementDetailsView.submittedByUserEmail />
</@fdsDataItems.dataItem>
