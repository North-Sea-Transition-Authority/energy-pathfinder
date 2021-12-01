<#include '../../layout.ftl'>
<#import '../_dashboard.ftl' as dashboard>

<#-- @ftlvariable name="infrastructureDashboardItem" type="uk.co.ogauthority.pathfinder.model.view.dashboard.infrastructure.InfrastructureProjectDashboardItemView" -->

<@dashboard.dashboardItemHeaderWrapper dashboardItem=infrastructureDashboardItem>
  <@fdsDataItems.dataItem dataItemListClasses="dashboard-item__data-list" >
    <@fdsDataItems.dataValues key="Field stage" value=infrastructureDashboardItem.fieldStage!""/>
    <@fdsDataItems.dataValues key="Field" value=infrastructureDashboardItem.fieldName!""/>
    <@fdsDataItems.dataValues key="Status" value=infrastructureDashboardItem.status!""/>
  </@fdsDataItems.dataItem>
</@dashboard.dashboardItemHeaderWrapper>