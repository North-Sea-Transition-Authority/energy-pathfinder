<#include '../layout.ftl'>

<#macro dashboard dashboardProjectHtmlItems resultSize="" showOperator=true>
  <#if !dashboardProjectHtmlItems?has_content>
    <@fdsInsetText.insetText>
      No projects match your filters or no projects have been created that you have access to.
    </@fdsInsetText.insetText>
  <#else>
    <@_resultCounter resultSize=resultSize/>
    <ol class="govuk-list dashboard-list">
      <#list dashboardProjectHtmlItems as dashboardProjectHtmlItem>
        <li class="govuk-list__item dashboard-list__item">
          <@noEscapeHtml.noEscapeHtml html=dashboardProjectHtmlItem.htmlContent />
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

<#macro dashboardItemHeaderWrapper dashboardItem showOperator=true>
  <div class="dashboard-item">
    <h3 class="dashboard-item__heading">
      <@userAction.userAction userAction=dashboardItem.dashboardLink ariaDescribeById="project-${dashboardItem.projectId}-update-tag"/>
      <#if dashboardItem.updateRequested>
        <@tag.tag tagClasses="govuk-tag--orange govuk-tag--float-right" id="project-${dashboardItem.projectId}-update-tag">
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
    <#nested/>
  </div>
</#macro>
