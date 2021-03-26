<#include '../../layout.ftl'>

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
<@fdsDataItems.dataItem>
  <@fdsDataItems.dataValues key="Submission date" value=projectManagementDetailView.submissionDate />
  <@fdsDataItems.dataValues key="Submitted by" value=projectManagementDetailView.submittedByUser />
  <@fdsDataItems.dataValues key="Submitter email" value=projectManagementDetailView.submittedByUserEmail />
</@fdsDataItems.dataItem>
