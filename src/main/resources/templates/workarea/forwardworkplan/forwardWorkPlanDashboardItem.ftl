<#include '../../layout.ftl'>
<#import '../_dashboard.ftl' as dashboard>
<#import '../../project/macros/forwardworkplan/forwardWorkPlanGuidance.ftl' as forwardWorkPlanGuidance>

<#-- @ftlvariable name="forwardWorkPlanDashboardItem" type="uk.co.ogauthority.pathfinder.model.view.dashboard.infrastructure.InfrastructureProjectDashboardItemView" -->
<#-- @ftlvariable name="forwardWorkPlanProjectLowerCaseDisplayName" type="String" -->
<#-- @ftlvariable name="infrastructureProjectLowerCaseDisplayName" type="String" -->
<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->
<#-- @ftlvariable name="startInfrastructureProjectUrl" type="String" -->

<@dashboard.dashboardItemHeaderWrapper dashboardItem=forwardWorkPlanDashboardItem>
  <@fdsDataItems.dataItem dataItemListClasses="dashboard-item__data-list" >
    <@fdsDataItems.dataValues key="Status" value=forwardWorkPlanDashboardItem.status!""/>
  </@fdsDataItems.dataItem>
  <div class="govuk-details-wrapper--no-margin-bottom">
    <@fdsDetails.summaryDetails summaryTitle="What is a ${forwardWorkPlanProjectLowerCaseDisplayName}?">
      <@forwardWorkPlanGuidance.introductionText
        forwardWorkPlanProjectTypeLowercaseDisplayName=forwardWorkPlanProjectLowerCaseDisplayName
        infrastructureProjectTypeLowercaseDisplayName=infrastructureProjectLowerCaseDisplayName
        startInfrastructureProjectUrl=startInfrastructureProjectUrl
      />
    </@fdsDetails.summaryDetails>
  </div>
</@dashboard.dashboardItemHeaderWrapper>