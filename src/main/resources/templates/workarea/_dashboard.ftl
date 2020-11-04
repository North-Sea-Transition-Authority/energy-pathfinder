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
        <@_dashboardItem dashboardItem=dashboardItem showOperator=showOperator/>
      </#list>
    <ol>
  </#if>
</#macro>

<#macro _resultCounter resultSize="">
  <h2 class="govuk-heading-s">
    ${resultSize} projects
  </h2>
</#macro>

<#macro _dashboardItem dashboardItem showOperator=true>
  <li class="govuk-list__item dashboard-list__item">
    <h3 class="govuk-heading-s govuk-!-margin-bottom-0">
      <@userAction.dashboardLink userAction=dashboardItem.projectLink/>
      <#if showOperator>
        <span class="govuk-caption-m">${dashboardItem.operatorName}</span>
      </#if>
    </h3>
    <@fdsDataItems.dataItem dataItemListClasses="govuk-!-margin-bottom-0 govuk-!-margin-top-1" >
      <@fdsDataItems.dataValues key="Field stage" value=dashboardItem.fieldStage!""/>
      <@fdsDataItems.dataValues key="Field" value=dashboardItem.fieldName!""/>
      <@fdsDataItems.dataValues key="Status" value=dashboardItem.status!""/>
    </@fdsDataItems.dataItem>
  </li>
</#macro>