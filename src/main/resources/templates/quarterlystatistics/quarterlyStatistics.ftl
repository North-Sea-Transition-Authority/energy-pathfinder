<#include '../layout.ftl'>
<#import '_fieldStageStatistics.ftl' as fieldStageStatistic>
<#import '_operatorProjects.ftl' as operatorProjects>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle topNavigation=true twoThirdsColumn=false>
  <@fdsInsetText.insetText>
    <p class="govuk-body">The quarterly statistics only include published projects</p>
  </@fdsInsetText.insetText>
  <@fdsTabs.tabs tabsHeading="Quarterly statistics">
    <@fdsTabs.tabList>
      <@fdsTabs.tab tabLabel="Overview" tabAnchor="overview" tabSelected=true/>
      <@fdsTabs.tab tabLabel="Operators" tabAnchor="operators" tabSelected=false/>
    </@fdsTabs.tabList>
    <@fdsTabs.tabContent tabAnchor="overview" tabSelected=true>
      <@fieldStageStatistic._fieldStageStatistics fieldStageStatistics=fieldStageStatistics />
    </@fdsTabs.tabContent>
    <@fdsTabs.tabContent tabAnchor="operators" tabSelected=false>
      <@operatorProjects._operators operatorProjectMap=operatorReportableProjects />
    </@fdsTabs.tabContent>
  </@fdsTabs.tabs>
</@defaultPage>