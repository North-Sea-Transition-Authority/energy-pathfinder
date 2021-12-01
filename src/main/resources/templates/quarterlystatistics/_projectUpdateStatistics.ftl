<#include '../layout.ftl'>

<#macro _projectUpdateStatisticGroup projectUpdateStatistics>
  <@statistic.statisticContainer>
    <#list projectUpdateStatistics as projectUpdateStatistic>
      <@statistic.statisticContainerItem headingText=projectUpdateStatistic.statisticPrompt>
        <@statistic.statistic
          prompt="Total"
          value=projectUpdateStatistic.totalProjects
        />
        <@statistic.statistic
          prompt="Updated this quarter"
          value=projectUpdateStatistic.totalProjectsUpdated
          promptClasses="govuk-!-margin-top-2"
        >
          <div class="statistic__tag">
            <@tag.tag tagClasses="govuk-tag--blue">
              ${projectUpdateStatistic.percentageOfProjectsUpdated?string(',###.##')}% of total
            </@tag.tag>
          </div>
        </@statistic.statistic>
      </@statistic.statisticContainerItem>
    </#list>
  </@statistic.statisticContainer>
</#macro>