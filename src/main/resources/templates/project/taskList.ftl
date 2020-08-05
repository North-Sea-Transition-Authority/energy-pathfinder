<#include '../layout.ftl'>

<@defaultPage htmlTitle="Pathfinder project task list" pageHeading="Pathfinder project" breadcrumbs=true>
    <@fdsTaskList.taskList>
        <@fdsTaskList.taskListSection sectionNumber="1" sectionHeadingText="Prepare project" >
            <@fdsTaskList.taskListItem itemUrl=springUrl(projectInformationUrl) itemText="Project information" completed=projectInformationCompleted useNotCompletedLabels=true />
            <@fdsTaskList.taskListItem itemUrl=springUrl(locationUrl) itemText="Location" completed=projectLocationCompleted useNotCompletedLabels=true />
        </@fdsTaskList.taskListSection>
    </@fdsTaskList.taskList>
</@defaultPage>