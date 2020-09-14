<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Upcoming tenders" pageHeading="Upcoming tenders" breadcrumbs=true>
    <#--    TODO add error summary and jumplinks -->
  <#list tenderViews as view>
    <#assign tenderName = "Upcoming tender " + view.getDisplayOrder()/>
    <h2 class="govuk-heading-l">${tenderName}</h2>
    <@tenderSummary.upcomingTenderSummary view=view tenderName=tenderName showValidationAndActions=true />
  </#list>
  <@fdsAction.link linkText="Add upcoming tender" linkUrl=springUrl(addTenderUrl) linkClass="govuk-button govuk-button--blue"/>

  <@fdsForm.htmlForm>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryLinkText="Back to task list" linkSecondaryAction=true linkSecondaryActionUrl=springUrl(backToTaskListUrl) />
  </@fdsForm.htmlForm>
</@defaultPage>