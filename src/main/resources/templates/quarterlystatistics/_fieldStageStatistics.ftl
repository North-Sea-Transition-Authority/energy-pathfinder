<#include '../layout.ftl'>

<#macro _fieldStageStatistics fieldStageStatistics>
  <@statistic.statisticContainer>
    <#list fieldStageStatistics as fieldStageStatistic>
      <@statistic.statisticContainerItem headingText=fieldStageStatistic.fieldStage>
        <@statistic.statistic prompt="Total projects" value=fieldStageStatistic.totalProjects />
        <@statistic.statistic
          prompt="Updated this quarter"
          value=fieldStageStatistic.totalProjectsUpdateThisQuarter
          promptClasses="govuk-!-margin-top-2"
        >
          <div class="statistic__tag">
            <@tag.tag tagClasses="govuk-tag--blue">
              ${fieldStageStatistic.percentageOfProjectsUpdated?string(',###.##')}% of total
            </@tag.tag>
          </div>
        </@statistic.statistic>
      </@statistic.statisticContainerItem>
    </#list>
  </@statistic.statisticContainer>
</#macro>