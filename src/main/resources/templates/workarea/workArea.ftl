<#include '../layout.ftl'>
<#import '_dashboard.ftl' as dashboard>
<#import '_dashboardFilters.ftl' as dashboardFilters>

<@defaultPage htmlTitle="Work area" pageHeading="Work area" pageHeadingClass="govuk-heading-xl govuk-!-margin-bottom-2" topNavigation=true fullWidthColumn=true>
  <#if showStartProject>
      <@fdsAction.link linkText="Create project" linkUrl=springUrl(startProjectUrl) linkClass="govuk-button"/>
  </#if>
  <@fdsSearch.searchPage>
    <@dashboardFilters.dashboardFilters/>

    <@fdsSearch.searchPageContent>
      <@dashboard.dashboard dashboardProjectItemViews=dashboardProjectItemViews resultSize=resultSize />
    </@fdsSearch.searchPageContent>
  </@fdsSearch.searchPage>
</@defaultPage>