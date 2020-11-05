<#include '../../layout.ftl'>
<#import './upcomingTenderSummary.ftl' as tenderSummary>

<@defaultPage htmlTitle="Upcoming tenders" pageHeading="Upcoming tenders" breadcrumbs=true>

  <#if errorSummary?has_content>
    <@fdsError.errorSummary errorItems=errorSummary />
  </#if>
  <div class="summary-list">
    <#if tenderViews?has_content>
      <#list tenderViews as view>
        <@tenderSummary.upcomingTenderSummary view=view showHeader=true showActions=true />
      </#list>
    <#else>
      <@setupProjectGuidance.minimumRequirementNotMetInset itemRequiredText="upcoming tender" linkUrl=""/>
    </#if>
  </div>

  <@fdsAction.link linkText="Add upcoming tender" linkUrl=springUrl(addTenderUrl) linkClass="govuk-button govuk-button--blue"/>

  <@fdsForm.htmlForm>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryLinkText="Back to task list" linkSecondaryAction=true linkSecondaryActionUrl=springUrl(backToTaskListUrl) />
  </@fdsForm.htmlForm>
</@defaultPage>