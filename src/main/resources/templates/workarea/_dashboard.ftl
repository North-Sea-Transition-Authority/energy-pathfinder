<#include '../layout.ftl'>

<#macro dashboard dashboardProjectItemViews resultSize="" showOperator=true>
  <#if !dashboardProjectItemViews?has_content>
    <@fdsInsetText.insetText>
      No projects match your filters or no projects have been created that you have access to.
    </@fdsInsetText.insetText>
  <#else>
    <@_resultCounter resultSize=resultSize/>
    <ol class="govuk-list dashboard-list">
      <#list dashboardProjectItemViews as dashboardItem>
        <li class="govuk-list__item dashboard-list__item">
          <@_dashboardItem dashboardItem=dashboardItem showOperator=showOperator/>
        </li>
      </#list>
    </ol>
  </#if>
</#macro>

<#macro _resultCounter resultSize="">
  <h2 class="govuk-heading-s">
    ${resultSize} projects
  </h2>
</#macro>

<#macro _dashboardItem dashboardItem showOperator=true>
  <div class="dashboard-item">
    <h3 class="dashboard-item__heading">
      <@userAction.userAction userAction=dashboardItem.dashboardLink/>
      <#if dashboardItem.updateRequested>
        <@tag.tag tagClasses="govuk-tag--orange govuk-tag--float-right">
          <#if dashboardItem.updateDeadlineDate?has_content>
            Update due by ${dashboardItem.updateDeadlineDate}
          <#else>
            Update requested
          </#if>
        </@tag.tag>
      </#if>
      <#if showOperator>
        <span class="govuk-caption-m">${dashboardItem.operatorName}</span>
      </#if>
    </h3>
    <@fdsDataItems.dataItem dataItemListClasses="dashboard-item__data-list" >
      <@fdsDataItems.dataValues key="Field stage" value=dashboardItem.fieldStage!""/>
      <@fdsDataItems.dataValues key="Field" value=dashboardItem.fieldName!""/>
      <@fdsDataItems.dataValues key="Status" value=dashboardItem.status!""/>
    </@fdsDataItems.dataItem>
  </div>
</#macro>