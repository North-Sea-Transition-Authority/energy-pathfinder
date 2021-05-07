<#include '../layout.ftl'>

<#if isUpdate>
  <#assign cancelDraftLinkText = "Cancel draft update">
<#else>
  <#assign cancelDraftLinkText = "Cancel draft ${projectTypeDisplayName}">
</#if>

<#assign pageTitle = taskListPageHeading />

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true>
  <@fdsAction.link
    linkText=cancelDraftLinkText
    linkUrl=springUrl(cancelDraftUrl)
    linkClass="govuk-button govuk-button--blue"
  />
  <@fdsTaskList.taskList>
    <#-- List over groups -->
    <#list groups as group>
      <@fdsTaskList.taskListSection sectionNumber=group.displayOrder sectionHeadingText=group.groupName >
        <#-- List over entries -->
        <#list group.taskListEntries as task>
          <@fdsTaskList.taskListItem itemUrl=springUrl(task.route) itemText=task.taskName completed=task.completed useNotCompletedLabels=task.usingCompletedLabels />
        </#list>
      </@fdsTaskList.taskListSection>
    </#list>
  </@fdsTaskList.taskList>
</@defaultPage>
