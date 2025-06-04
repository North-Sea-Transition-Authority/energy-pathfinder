<#include '../layout.ftl'>
<#import '_projectUpdateStatistics.ftl' as projectUpdateStatistic>
<#import '_operatorProjects.ftl' as operatorProjects>

<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="projectUpdateStatistics" type="java.util.List<uk.co.ogauthority.pathfinder.service.quarterlystatistics.ProjectUpdateStatistic>" -->
<#-- @ftlvariable name="operatorReportableProjects" type="java.util.Map<String, java.util.List<uk.co.ogauthority.pathfinder.service.quarterlystatistics.ReportableProjectView>>" -->

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle topNavigation=true twoThirdsColumn=false>
  <@fdsInsetText.insetText>
    <p class="govuk-body">The quarterly statistics only include projects which are published</p>
  </@fdsInsetText.insetText>
  <@fdsTabs.tabs tabsHeading="Quarterly statistics">
    <@fdsTabs.tabList>
      <@fdsTabs.tab tabLabel="Overview" tabAnchor="overview"/>
      <@fdsTabs.tab tabLabel="Operators/Developers" tabAnchor="operators"/>
    </@fdsTabs.tabList>
    <@fdsTabs.tabContent tabAnchor="overview">
      <@projectUpdateStatistic._projectUpdateStatisticGroup projectUpdateStatistics=projectUpdateStatistics />
    </@fdsTabs.tabContent>
    <@fdsTabs.tabContent tabAnchor="operators">
      <@operatorProjects._operators operatorProjectMap=operatorReportableProjects />
    </@fdsTabs.tabContent>
  </@fdsTabs.tabs>
</@defaultPage>
