<#include '../layout.ftl'>

<@defaultPage htmlTitle="Pathfinder project task list" pageHeading="Pathfinder project" breadcrumbs=true>
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