<#include '../../layout.ftl'>
<#import '../_dashboard.ftl' as dashboard>

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
      <p class="govuk-body">
        Maintenance and Operations (M&O) activity accounts for a significant annual expenditure in offshore operations
        with a multitude of contracts being offered or renewed on a regular basis. The ${forwardWorkPlanProjectLowerCaseDisplayName}
        facility is specifically designed to provide the supply chain with visibility of M&O contract opportunities (OPEX expenditure).
      </p>
      <p class="govuk-body">
        Capital expenditure (CAPEX) for new and decommissioning projects should be provided on an
        <@fdsAction.link
          linkText="${service.serviceName} ${infrastructureProjectLowerCaseDisplayName}"
          linkUrl=springUrl(startInfrastructureProjectUrl)
        />
        instead of a ${forwardWorkPlanProjectLowerCaseDisplayName}.
      </p>
    </@fdsDetails.summaryDetails>
  </div>
</@dashboard.dashboardItemHeaderWrapper>