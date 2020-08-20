<#include '../layout.ftl'>

<@defaultPage htmlTitle="Pathfinder project task list" pageHeading="Pathfinder project" breadcrumbs=true>
    <@fdsTaskList.taskList>
        <@fdsTaskList.taskListSection sectionNumber="1" sectionHeadingText="Project operator" >
            <@fdsTaskList.taskListItem itemUrl=springUrl(changeOperatorUrl) itemText=changeOperatorName completed=changeOperatorCompleted useNotCompletedLabels=true />
        </@fdsTaskList.taskListSection>
        <@fdsTaskList.taskListSection sectionNumber="2" sectionHeadingText="Prepare project" >
            <@fdsTaskList.taskListItem itemUrl=springUrl(projectInformationUrl) itemText=projectInformationText completed=projectInformationCompleted useNotCompletedLabels=true />
            <@fdsTaskList.taskListItem itemUrl=springUrl(locationUrl) itemText=projectLocationText completed=projectLocationCompleted useNotCompletedLabels=true />
        </@fdsTaskList.taskListSection>
    </@fdsTaskList.taskList>
</@defaultPage>