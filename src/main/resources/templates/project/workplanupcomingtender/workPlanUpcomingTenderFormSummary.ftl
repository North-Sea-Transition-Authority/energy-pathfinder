<#include '../../layout.ftl'>
<#import '_workPlanUpcomingTenderSummary.ftl' as tenderSummary>

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true errorItems=errorSummary>

  <#list tenderViews as view>
    <@tenderSummary.workPlanUpcomingTenderSummary view=view showHeader=true showActions=true/>
  </#list>

  <@fdsAction.link linkText="Add upcoming tender" linkUrl=springUrl(addUpcomingTenderUrl) linkClass="govuk-button govuk-button--blue"/>

</@defaultPage>