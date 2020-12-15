<#include '../layout.ftl'>
<#import '_dashboard.ftl' as dashboard>
<#import '_dashboardFilters.ftl' as dashboardFilters>

<@defaultPage htmlTitle="Work area" pageHeading="Work area" pageHeadingClass="govuk-heading-xl govuk-!-margin-bottom-2" topNavigation=true fullWidthColumn=true>
  <@userAction.userAction userAction=startProjectButton/>

  <@fdsSearch.searchPage>
    <@dashboardFilters.dashboardFilters includeOperatorFilter=includeOperatorFilter/>

    <@fdsSearch.searchPageContent>
      <@dashboard.dashboard dashboardProjectItemViews=dashboardProjectItemViews resultSize=resultSize />
    </@fdsSearch.searchPageContent>
  </@fdsSearch.searchPage>
</@defaultPage>