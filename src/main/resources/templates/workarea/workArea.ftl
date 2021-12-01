<#include '../layout.ftl'>
<#import '_dashboard.ftl' as dashboard>
<#import '_dashboardFilters.ftl' as dashboardFilters>

<#-- @ftlvariable name="startProjectButton" type="uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton" -->
<#-- @ftlvariable name="clearFilterUrl" type="String" -->
<#-- @ftlvariable name="filterType" type="String" -->
<#-- @ftlvariable name="resultSize" type="String" -->
<#-- @ftlvariable name="dashboardProjectHtmlItems" type="java.util.List<uk.co.ogauthority.pathfinder.service.dashboard.DashboardProjectHtmlItem>" -->

<@defaultPage
  htmlTitle="Work area"
  pageHeading="Work area"
  pageHeadingClass="govuk-heading-xl govuk-!-margin-bottom-2"
  topNavigation=true
  fullWidthColumn=true
>
  <@userAction.userAction userAction=startProjectButton/>
  <@fdsSearch.searchPage>
    <@dashboardFilters.dashboardFilters clearFilterUrl=clearFilterUrl filterType=filterType/>
    <@fdsSearch.searchPageContent>
      <@dashboard.dashboard dashboardProjectHtmlItems=dashboardProjectHtmlItems resultSize=resultSize />
    </@fdsSearch.searchPageContent>
  </@fdsSearch.searchPage>
</@defaultPage>