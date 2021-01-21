<#include '../layout.ftl'>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle topNavigation=true twoThirdsColumn=false>
  <@fdsInsetText.insetText>
    <p class="govuk-body">The quarterly statistics only include published projects</p>
  </@fdsInsetText.insetText>
  <@statistic.statisticContainer>
    <#list quarterlyStatistics as quarterlyStatistic>
      <@statistic.statisticContainerItem headingText=quarterlyStatistic.fieldStage>
        <@statistic.statistic prompt="Total projects" value=quarterlyStatistic.totalProjects />
        <@statistic.statistic
          prompt="Updated this quarter"
          value=quarterlyStatistic.totalProjectsUpdateThisQuarter
          promptClasses="govuk-!-margin-top-2"
        >
          <div class="statistic__tag">
            <@tag.tag tagClasses="govuk-tag--blue">
              ${quarterlyStatistic.percentageOfProjectsUpdated?string(',###.##')}% of total
            </@tag.tag>
          </div>
        </@statistic.statistic>
      </@statistic.statisticContainerItem>
    </#list>
  </@statistic.statisticContainer>
</@defaultPage>

