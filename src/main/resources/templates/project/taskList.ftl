<#include '../layout.ftl'>

<@defaultPage htmlTitle="Pathfinder project task list" pageHeading="Pathfinder project" breadcrumbs=true>
  <@fdsTaskList.taskList>
    <@fdsTaskList.taskListSection sectionNumber="1" sectionHeadingText="Project operator" >
      <@fdsTaskList.taskListItem itemUrl=springUrl(changeOperatorUrl) itemText=changeOperatorName completed=changeOperatorCompleted useNotCompletedLabels=true />
    </@fdsTaskList.taskListSection>
    <@fdsTaskList.taskListSection sectionNumber="2" sectionHeadingText="Prepare project" >
      <@fdsTaskList.taskListItem itemUrl=springUrl(projectInformationUrl) itemText=projectInformationText completed=projectInformationCompleted useNotCompletedLabels=true />
      <@fdsTaskList.taskListItem itemUrl=springUrl(locationUrl) itemText=projectLocationText completed=projectLocationCompleted useNotCompletedLabels=true />
      <@fdsTaskList.taskListItem itemUrl=springUrl(upcomingTendersUrl) itemText=upcomingTendersText completed=upcomingTendersCompleted useNotCompletedLabels=true />
      <@fdsTaskList.taskListItem
        itemUrl=springUrl(awardedContractsUrl)
        itemText=awardedContractsText
        completed=awardedContractsCompleted
        useNotCompletedLabels=true
      />
      <@fdsTaskList.taskListItem itemUrl=springUrl(collaborationOpportunitiesUrl) itemText=collaborationOpportunitiesText completed=collaborationOpportunitiesCompleted useNotCompletedLabels=true />
      <@fdsTaskList.taskListItem
        itemUrl=springUrl(decommissionedWellsUrl)
        itemText=decommissionedWellsText
        completed=decommissionedWellsCompleted
        useNotCompletedLabels=true
      />
      <@fdsTaskList.taskListItem
        itemUrl=springUrl(platformsFpsosUrl)
        itemText=platformsFpsosText
        completed=platformsFpsosCompleted
        useNotCompletedLabels=true
      />
      <@fdsTaskList.taskListItem
        itemUrl=springUrl(subseaInfrastructureUrl)
        itemText=subseaInfrastructureText
        completed=subseaInfrastructureCompleted
        useNotCompletedLabels=true
      />
      <@fdsTaskList.taskListItem
        itemUrl=springUrl(integratedRigUrl)
        itemText=integratedRigText
        completed=integratedRigCompleted
        useNotCompletedLabels=true
      />
      <@fdsTaskList.taskListItem
      itemUrl=springUrl(decommissionedPipelinesUrl)
      itemText=decommissionedPipelinesText
      completed=decommissionedPipelinesCompleted
      useNotCompletedLabels=true
      />
    </@fdsTaskList.taskListSection>
    <@fdsTaskList.taskListSection sectionNumber="3" sectionHeadingText="Submit project" >
      <@fdsTaskList.taskListItem
        itemUrl=springUrl(reviewAndSubmitLink)
        itemText="Review and submit"
      />
    </@fdsTaskList.taskListSection>
  </@fdsTaskList.taskList>
</@defaultPage>