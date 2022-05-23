<#include '../layout.ftl'>

<#-- @ftlvariable name="isUpdate" type="Boolean" -->
<#-- @ftlvariable name="projectTypeDisplayNameLowercase" type="String" -->
<#-- @ftlvariable name="taskListPageHeading" type="String" -->
<#-- @ftlvariable name="isCancellable" type="Boolean" -->
<#-- @ftlvariable name="cancelDraftUrl" type="String" -->
<#-- @ftlvariable name="groups" type="java.util.List<uk.co.ogauthority.pathfinder.model.view.tasks.TaskListGroup>" -->
<#-- @ftlvariable name="canDisplayEmail" type="Boolean" -->
<#-- @ftlvariable name="hasTaskListGroups" type="Boolean" -->
<#-- @ftlvariable name="ownerEmail" type="String" -->

<#if isUpdate>
  <#assign cancelDraftLinkText = "Cancel draft update">
<#else>
  <#assign cancelDraftLinkText = "Cancel draft ${projectTypeDisplayNameLowercase}">
</#if>

<#assign operatorContactText>
  <#if canDisplayEmail>
    <@mailTo.mailToLink linkText="${projectTypeDisplayNameLowercase} operator" mailToEmailAddress=ownerEmail/>
  <#else>
    ${projectTypeDisplayNameLowercase} operator
  </#if>
</#assign>

<#assign pageTitle = taskListPageHeading />

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true>

  <#if isCancellable>
    <@fdsAction.link
      linkText=cancelDraftLinkText
      linkUrl=springUrl(cancelDraftUrl)
      linkClass="govuk-button govuk-button--blue"
    />
  </#if>
  <@fdsTaskList.taskList>
    <#-- List over groups -->
    <#if hasTaskListGroups>
      <#list groups as group>
        <@fdsTaskList.taskListSection sectionNumber=group.displayOrder?c sectionHeadingText=group.groupName >
        <#-- List over entries -->
          <#list group.taskListEntries as task>
            <@fdsTaskList.taskListItem itemUrl=springUrl(task.route) itemText=task.taskName completed=task.completed useNotCompletedLabels=task.usingCompletedLabels />
          </#list>
        </@fdsTaskList.taskListSection>
      </#list>
    <#else>
      <@fdsInsetText.insetText>
        You do not have access to any sections of this ${projectTypeDisplayNameLowercase}.
        Contact the ${operatorContactText}
        to gain access
      </@fdsInsetText.insetText>
    </#if>
  </@fdsTaskList.taskList>
</@defaultPage>
